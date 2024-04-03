package com.example.team16project.repository.reply;

import com.example.team16project.domain.reply.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Optional<Reply> findByReplyId(Long id);
  
    List<Reply> findByArticleArticleId(Long articleId);
    List<Reply> findByReplyReplyId(Long replyId);
    List<Reply> findByArticleArticleIdAndReplyReplyId(Long articleArticleId, Long replyReplyId);

}
