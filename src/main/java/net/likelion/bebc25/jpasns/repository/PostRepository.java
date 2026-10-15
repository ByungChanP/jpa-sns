package net.likelion.bebc25.jpasns.repository;

import net.likelion.bebc25.jpasns.domain.Member;
import net.likelion.bebc25.jpasns.domain.Post;
import net.likelion.bebc25.jpasns.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정회원이 작성한 게시글 목록을 최신순 조회 (여러건/ 단건인 경우 optional)
    Page<Post> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    // TODO: 전체 게시글 목록을 최신 등록순으로 정렬 조회
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // TODO: 검색어 키워드를 받아서 해당 본문에 키워드가 포함된 게시글을 검색
    Page<Post> findByContentContainingOrderByCreatedAtDesc(String keword, Pageable pageable);

    // TODO: 회원 식별자를 조건으로 검색어 키워드를 받아서 해당 본문에 키워드가 포함된 게시글을 검색
    List<Post> findByMemberAndContentContainingOrderByCreatedAtDesc(Member member, String keword);

    @Query("SELECT p FROM Post p JOIN p.member m WHERE m.id = :memberId AND p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    Page<Post> findByMemberAndKeyword(
            @Param("memberId") Member member,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 본문 키워드 포함 검색
    List<Post> findByContentContaining(String keyword);

    // 특정 좋아요 수 이상의 인기 게시글 조회
    List<Post> findByLikeCountGreaterThanEqual(int minLikeCount);

    // 특정 회원의 특정 좋아요 수 이상 게시글 최신순 정렬 조회 (조건 누적으로 메서드명이 길어진 쿼리 메서드)
    List<Post> findByMember_IdAndLikeCountGreaterThanEqualOrderByCreatedAtDesc(
            Long memberId,
            int minLikeCount
    );
    // 특정 회원의 특정 좋야요 수 이상 게시글 최신순 정렬 조회
    @Query("SELECT p FROM Post p JOIN p.member m WHERE m.id = :memberId AND p.likeCount >= :minLikeCount ORDER BY p.createdAt DESC")
    List<Post> findByMemberAndMinLike(
            @Param("memberId") Long memberId,
            @Param("minLikeCount") int minLikeCount
    );

    @Query("""
        SELECT new net.likelion.bebc25.jpasns.dto.PostDto$SummaryResponse(
            p.id,
            m.nickname,
            p.content,
            p.likeCount
        )
        FROM Post p
        JOIN p.member m
        WHERE  m.id = :memberId
    """)
    List<PostDto.SummaryResponse> findSummariesByMemberId(Long memberId);
}
