package com.example.team16project.service.reply;

import com.example.team16project.domain.article.Article;
import com.example.team16project.domain.reply.Reply;
import com.example.team16project.domain.user.User;
import com.example.team16project.dto.reply.request.ReReplyCreateForm;
import com.example.team16project.dto.reply.request.ReReplyUpdateRequest;
import com.example.team16project.dto.reply.request.ReplyCreateForm;
import com.example.team16project.dto.reply.request.ReplyUpdateRequest;
import com.example.team16project.dto.reply.response.ReplyDto;
import com.example.team16project.repository.article.ArticleRepository;
import com.example.team16project.repository.reply.ReplyRepository;
import com.example.team16project.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // 댓글 생성 로직
    @Transactional
    @Override
    public void saveReply(ReplyCreateForm form, Principal principal) {

        // principal.getName(); // id(Email)를 가져옴

        // principal 검증 로직 추가 -> 로그인이 되었는지
        if(principal == null){
            throw new NoSuchElementException("로그인을 해야 댓글을 작성할 수 있습니다.");
        }

        User user = userRepository.findByEmail(principal.getName())  //principal.getName()을 통해 user의 email을 가져옴
                .orElseThrow(IllegalArgumentException::new);

        Article article = articleRepository.findByArticleId(form.getArticleId())
                .orElseThrow(IllegalArgumentException::new);
      
        replyRepository.save(new Reply(article, user, form.getComments()));
    }

    // 대댓글 생성 로직
    @Transactional
    @Override
    public void saveReReply(ReReplyCreateForm form, Principal principal){

        // principal 검증 로직 추가 -> 로그인이 되었는지
        if(principal == null){
            throw new NoSuchElementException("로그인을 해야 대댓글을 작성할 수 있습니다.");
        }

        User user = userRepository.findByEmail(principal.getName())  //principal.getName()을 통해 user의 email을 가져옴
                .orElseThrow(IllegalArgumentException::new);

        Article article = articleRepository.findByArticleId(form.getArticleId())
                .orElseThrow(IllegalArgumentException::new);

        Reply reply = replyRepository.findByReplyId(form.getParentReplyId())
                .orElseThrow(IllegalArgumentException::new);

        replyRepository.save(new Reply(user, article, reply, form.getReComments()));


    }

    @Override
    public long countHowManyReplies(Long articleId) {
        return replyRepository.countByArticleArticleId(articleId);
    }

    // 댓글 수정 로직
    @Transactional
    @Override
    public void updateReply(ReplyUpdateRequest request, Principal principal)  {

        // principal 검증 로직 추가 -> 로그인이 되었는지
        if (principal == null) {
            throw new NoSuchElementException("로그인을 해야 댓글을 수정할 수 있습니다.");
        }

        Reply reply = replyRepository.findByReplyId(request.getReplyId())
                .orElseThrow(IllegalArgumentException::new);

        // 댓글을 쓴 사람만이 글을 수정할 수 있게 하는 로직 추가
        if (!principal.getName().equals(reply.getUser().getEmail())) {
            throw new AccessDeniedException("댓글을 작성한 작성자만 삭제할 수 있습니다."); // 403 Error
        }

        reply.updateComments(request.getComments());
        // 변경감지(dirty check) 작동하여 comments field 변경 후 따로 저장해 줄 필요 업음
    }

    @Transactional
    @Override
    public void updateReReply(ReReplyUpdateRequest request, Principal principal)  {

        // principal 검증 로직 추가 -> 로그인이 되었는지
        if (principal == null) {
            throw new NoSuchElementException("로그인을 해야 댓글을 수정할 수 있습니다.");
        }

        Reply reply = replyRepository.findByReplyId(request.getReplyId())
                .orElseThrow(IllegalArgumentException::new);

        // 댓글을 쓴 사람만이 글을 지울 수 있게 하는 로직 추가
        if (!principal.getName().equals(reply.getUser().getEmail())) {
            throw new AccessDeniedException("댓글을 작성한 작성자만 삭제할 수 있습니다."); // 403 Error
        }

        reply.updateComments(request.getComments());
        // 변경감지(dirty check) 작동하여 comments field 변경 후 따로 저장해 줄 필요 업음
    }

    // 댓글 삭제 로직
    @Transactional
    @Override
    public void deleteReply(Long replyId, Principal principal)  {

        // principal 검증 로직 추가 -> 로그인이 되었는지
        if(principal == null){
            throw new NoSuchElementException("로그인을 해야 댓글을 삭제할 수 있습니다.");
        }

        List<Reply> replies = replyRepository.findByReplyIdOrReplyReplyId(replyId, replyId)
                .orElseThrow(IllegalArgumentException::new);

        // 댓글을 쓴 사람만이 글을 지울 수 있게 하는 로직 추가
        // replies.get(0)는 List<Reply> replies의 첫번째 요소를 의미
        if(!principal.getName().equals(replies.get(0).getUser().getEmail())){
            throw new AccessDeniedException("댓글을 작성한 작성자만 삭제할 수 있습니다."); // 403 Error
        }

        replyRepository.deleteAll(replies);
    }

    // 대댓글 삭제 로직
    @Transactional
    @Override
    public void deleteReReply(Long replyId, Principal principal)  {

        // principal 검증 로직 추가 -> 로그인이 되었는지
        if(principal == null){
            throw new NoSuchElementException("로그인을 해야 댓글을 삭제할 수 있습니다.");
        }
        Reply reply = replyRepository.findByReplyId(replyId)
                .orElseThrow(IllegalArgumentException::new);

        // 댓글을 쓴 사람만이 글을 지울 수 있게 하는 로직 추가
        if(!principal.getName().equals(reply.getUser().getEmail())){
            throw new AccessDeniedException("댓글을 작성한 작성자만 삭제할 수 있습니다."); // 403 Error
        }
        replyRepository.delete(reply);
    }
  
    // 댓글 조회 로직

    @Transactional
    @Override
    public List<ReplyDto> getAllReplysOnArticle(Long articleId) {
        List<Reply> byArticleArticleIdAndReplyReplyId = replyRepository.findByArticleArticleIdAndReplyReplyId(articleId, null);
        List<ReplyDto> collect = byArticleArticleIdAndReplyReplyId.stream().map(reply -> ReplyDto.toDto(reply,
                replyRepository.findByReplyReplyId(reply.getReplyId()).stream().map(reply1 -> new ReplyDto(reply1.getReplyId(), reply1.getUser().getNickname(), reply1.getComments(), null))
                        .collect(Collectors.toList()))).collect(Collectors.toList());
        return collect;
    }
}
