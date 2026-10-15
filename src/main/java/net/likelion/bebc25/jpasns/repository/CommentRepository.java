package net.likelion.bebc25.jpasns.repository;

import net.likelion.bebc25.jpasns.domain.Comment;
import net.likelion.bebc25.jpasns.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글에 작성된 댓글 목록을 조회(작성일시의 오름차순으로 정렬)
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
}