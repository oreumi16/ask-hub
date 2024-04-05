package com.example.team16project.controller.article;

import com.example.team16project.domain.user.User;
import com.example.team16project.dto.article.request.ArticleWithIdForm;
import com.example.team16project.dto.article.response.ArticleDto;
import com.example.team16project.dto.article.request.ArticleForm;
import com.example.team16project.dto.reply.response.ReplyDto;
import com.example.team16project.exception.LoginRequiredException;
import com.example.team16project.repository.user.UserRepository;
import com.example.team16project.service.article.ArticleService;
import com.example.team16project.domain.article.Article;
import com.example.team16project.service.reply.ReplyService;
import com.example.team16project.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@Tag(name = "Article", description = "게시글 API")
@RequiredArgsConstructor
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final UserRepository userRepository;
    private final ReplyService replyService;

    @Operation(summary = "게시글 전체 보기",
            description = "페이지 번호와 함께 게시글 전체를 볼 수 있습니다")
    @Parameter(name = "page", description = "페이지 번호", example = "2")
    @ApiResponse(responseCode = "200", description = "요청에 성공했습니다", content = @Content(mediaType = "text/html"))
    @GetMapping("/articles")
    public String articles(@RequestParam(defaultValue = "1") int page, Model model) {

        if (page < 1) {
            page = 1;
        }

        setPaginationAttributes(model, page,
                articleService.getTotalPages(PaginationUtil.PageSize), articleService.getArticles(page, PaginationUtil.PageSize));

        return "article/articles";
    }

    private void setPaginationAttributes(Model model, int page, int totalPages, List<ArticleDto> list) {

        int startIdx = PaginationUtil.calculateStartIndex(page);
        int endIdx = PaginationUtil.calculateEndIndex(page, totalPages);

        model.addAttribute("articles", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("startIdx", startIdx);
        model.addAttribute("endIdx", endIdx);
        model.addAttribute("totalPages", totalPages);
    }

    @Operation(summary = "게시글 상세 보기",
            description = "게시글을 클릭하면 상세 페이지를 볼 수 있습니다")
    @Parameter(name = "articleId", description = "페이지 번호", example = "1")
    @ApiResponse(responseCode = "200", description = "요청에 성공했습니다", content = @Content(mediaType = "text/html"))
    @GetMapping("/article")
    public String detail(@RequestParam(value = "id", required = true) Long articleId, Principal principal, Model model){

        ArticleDto article = articleService.getArticle(articleId);
        if (principal != null) {
            User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            if (article.getUserId().equals(user.getUserId()))
                model.addAttribute("identified", true);
        }
        model.addAttribute("article", article);
        List<ReplyDto> allReplysOnArticle = replyService.getAllReplysOnArticle(articleId);
        model.addAttribute("replys", allReplysOnArticle);

        return "article/detail";
    }

    @Operation(summary = "게시글 작성하는 폼", description = "게시글을 작성할 수 있는 폼을 불러옵니다")
    @ApiResponse(responseCode = "200", description = "요청에 성공했습니다", content = @Content(mediaType = "text/html"))
    @GetMapping("/article/form")
    public String getForm(Model model){
        return "article/form";
    }

    @Operation(summary = "게시글 작성하는 폼", description = "게시글을 작성할 수 있는 폼을 불러옵니다")
    @ApiResponse(responseCode = "200", description = "요청에 성공했습니다", content = @Content(mediaType = "text/plain;charset=UTF-8"))
    @ResponseBody
    @PostMapping("/article")
    public String saveForm(@Valid @RequestBody ArticleForm articleForm, Principal principal, Model model) throws AuthenticationException {

        if (principal == null)
            throw new AuthenticationException("로그인이 필요합니다");

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Article article = articleService.saveArticle(articleForm, user);
        return "/article?id=" + article.getArticleId();
    }

    @GetMapping("/article/edit")
    public String edit(@RequestParam(value = "id", required = true) Long articleId, Principal principal, Model model) throws LoginRequiredException, IllegalAccessException {

        if(principal == null) {
            throw new LoginRequiredException("로그인 해야합니다");
        }

        ArticleDto article = articleService.getArticle(articleId);
        User user = (User)((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        if (!article.getUserId().equals(user.getUserId())) {
            throw new IllegalAccessException("권한이 없습니다");
        }

        model.addAttribute("article", article);
        model.addAttribute("initialValue", article.getContents());

        return "article/edit";
    }

    @ResponseBody
    @PutMapping("/article")
    public String update(@Valid @RequestBody ArticleWithIdForm articleWithIdForm, Principal principal, Model model) throws IllegalAccessException, LoginRequiredException {

        if(principal == null) {
            throw new LoginRequiredException("로그인 해야합니다");
        }

        ArticleDto article = articleService.getArticle(articleWithIdForm.getArticleId());
        User user = (User)((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        if (!article.getUserId().equals(user.getUserId())) {
            throw new IllegalAccessException("잘못된 접근입니다");
        }
        articleService.editArticle(articleWithIdForm);

        return "/article?id=" + articleWithIdForm.getArticleId();
    }

    @ResponseBody
    @DeleteMapping("/article")
    public String delete(@RequestParam(value = "id") Long articleId, Principal principal, Model model) throws LoginRequiredException, IllegalAccessException {

        if(principal == null) {
            throw new LoginRequiredException("로그인 해야합니다");
        }

        ArticleDto article = articleService.getArticle(articleId);
        User user = (User)((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        if (!article.getUserId().equals(user.getUserId())) {
            throw new IllegalAccessException("권한이 없습니다");
        }

        articleService.deleteArticle(articleId);

        return "/articles";
    }


}
