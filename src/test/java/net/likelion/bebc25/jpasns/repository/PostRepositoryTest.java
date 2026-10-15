package net.likelion.bebc25.jpasns.repository;

import net.likelion.bebc25.jpasns.domain.Post;
import net.likelion.bebc25.jpasns.dto.PostDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("본문 키워드 포함 조건 검색")
    void findByContentContaining() {
        // Given: 검색 키워드 지정
        String keyword = "스프링 부트";

        // When: 본문 LIKE %키워드% 조회
        List<Post> posts = postRepository.findByContentContaining(keyword);

        // Then: 반환된 모든 게시글 본문에 키워드 포함 여부 확인
        assertThat(posts).isNotEmpty();
        assertThat(posts).allMatch(post -> post.getContent().contains(keyword));
    }

    @Test
    @DisplayName("특정 좋아요 수 이상인 인기 게시글 목록 필터링 조회")
    void findByLikeCountGreaterThanEqual() {
        // Given: 최소 좋아요 기준 수치
        int minLikeCount = 3;

        // When: 대소 비교(>=) 조건 조회
        List<Post> posts = postRepository.findByLikeCountGreaterThanEqual(minLikeCount);

        // Then: 조건 수치 이상 여부 검증
        assertThat(posts).isNotEmpty();
        assertThat(posts).allMatch(post -> post.getLikeCount() >= minLikeCount);
    }

    @Test
    @DisplayName("특정 회원의 좋아요 수 조건 이상 게시글 최신순 정렬 조회")
    void findByMember_IdAndLikeCountGreaterThanEqualOrderByCreatedAtDesc() {
        // Given: 회원 식별자 및 최소 좋아요 조건
        Long memberId = 2L;
        int minLikeCount = 3;

        // When: 다중 조건 및 정렬 결합 쿼리 메서드 호출
        List<Post> posts = postRepository.findByMemberAndMinLike(
                memberId,
                minLikeCount
        );

        // Then: 다중 조건 충족 및 등록일시 내림차순 정렬 상태 검증
        assertThat(posts).isNotEmpty();
        assertThat(posts).allMatch(post -> post.getMember().getId().equals(memberId) && post.getLikeCount() >= minLikeCount);
        assertThat(posts).isSortedAccordingTo((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
    }

    @Test
    @DisplayName("JPQL new 생성자 기반 DTO 직접 매핑 조회")
    void findSummariesByMemberId() {
        Long memberId = 1L;

        List<PostDto.SummaryResponse> summaries = postRepository.findSummariesByMemberId(memberId);

        assertThat(summaries).isNotEmpty();
        assertThat(summaries).allMatch(dto -> dto.postId() != null && dto.memberNickname() != null);
    }
}