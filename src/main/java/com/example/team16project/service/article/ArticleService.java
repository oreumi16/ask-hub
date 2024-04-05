package com.example.team16project.service.article;


import com.example.team16project.domain.article.Article;
import com.example.team16project.domain.user.User;
import com.example.team16project.dto.article.request.ArticleWithIdForm;
import com.example.team16project.dto.article.response.ArticleDto;
import com.example.team16project.dto.article.request.ArticleForm;

import java.security.Principal;
import java.util.List;

public interface ArticleService {
    ArticleDto getArticle(Long articleId);
    List<ArticleDto> getArticles(int page, int pageSize);
    int getTotalPages(int pageSize);
    Article saveArticle(ArticleForm articleForm, User user);
    void editArticle(ArticleWithIdForm articleWithIdForm);
    void deleteArticle(Long articleId);
}

