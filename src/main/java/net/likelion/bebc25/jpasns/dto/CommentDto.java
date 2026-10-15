package net.likelion.bebc25.jpasns.dto;

import net.likelion.bebc25.jpasns.domain.Comment;

import java.time.LocalDateTime;

// 댓글 정보를 담는 DTO 모음
public class CommentDto {

    // 댓글 등록 요청시 전달되는 DTO
    public record CreateRequest(
            String content
    ) {}

    // 댓글 응답용 DTO
    public record Response(
            Long id,
            String content,
            String authorNickname,
            LocalDateTime createdAt
    ){
        // Comment 엔티티를 Response DTO로 변환
        public static Response from(Comment comment){
            return new Response(
                    comment.getId(),
                    comment.getContent(),
                    comment.getMember().getNickname(),
                    comment.getCreatedAt()
            );
        }
    }
}