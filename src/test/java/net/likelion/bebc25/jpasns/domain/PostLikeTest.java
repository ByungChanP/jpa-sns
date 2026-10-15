package net.likelion.bebc25.jpasns.domain;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PostLikeTest {

    @Autowired
    private EntityManager em;
    private Long memberId;
    private Long postId;

    @BeforeEach
    void setUp(){

        // 회원 등록
        Member member = new Member("test1@test.com", "1234", "테스터 1");
        em.persist(member); // member insert
        this.memberId = member.getId();


        Post post = new Post(member, "첫번째 게시글");
        em.persist(post);
        this.postId = post.getId();

        em.flush();
        em.clear();

    }

    @Test
    @DisplayName("좋아요 등록과 조회")
    void postLikeSaveAndFind(){
        // 1. 회원과 게시글 조회
        Member findMember = em.find(Member.class, memberId);
        Post findPost = em.find(Post.class, postId);

        PostLike postLike = new PostLike(findMember, findPost);
        em.persist(postLike);

        em.flush();
        em.clear();

        // 좋아요 조회 후 작성자 정보, 게시글 정보가 등록한 정보와 일치하는지 검증
        assertThat((findMember.getId())).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo("test1@test.com");
        assertThat(findMember.getNickname()).isEqualTo("테스터 1");

        assertThat(findPost.getId()).isNotNull();
        assertThat(findPost.getMember().getId()).isEqualTo(memberId);
        assertThat(findPost.getContent()).isEqualTo("첫번째 게시글");

        // 좋아요를 등록한 회원 아이디가 동일한지 여부
        assertThat(postLike.getMember().getId()).isNotNull();
        assertThat(postLike.getMember().getId()).isEqualTo(memberId);
        assertThat(postLike.getMember().getId()).isEqualTo(findPost.getMember().getId());

        // 좋아요된 post_id와 등록된 post_id 서로 동일한지 여부
        assertThat(postLike.getPost().getId()).isEqualTo(postId);

        // 게시글 정보가 등록한 정보와 일치하는지 검증
        assertThat(postLike.getPost().getContent()).isEqualTo("첫번째 게시글");

        // 생성일시, 수정일시가 존재하는지 검증
        assertThat(postLike.getCreatedAt()).isNotNull();
        assertThat(postLike.getUpdatedAt()).isNotNull();

    }
    @Test
    @DisplayName("동일 회원의 동일 게시글에 대한 좋아요 중복 검증")
    void duplicatePostLikeThrowException(){
        Member findMember = em.find(Member.class, memberId);
        Post findPost = em.find(Post.class, postId);
        PostLike postLike = new PostLike(findMember, findPost);

        em.persist(postLike);

        em.flush();
        em.clear();

        // 1. 회원, 게시글 조회
        assertThat(findMember.getNickname()).isEqualTo(findPost.getMember().getNickname());

        // 2. 좋아요 생성, 등록
        assertThat(postLike.getId()).isNotNull();

        // 3. 동일한 사용자가 동일한 게시글에 대한 좋아요 생성
        PostLike postLike2 = new PostLike(findMember, findPost);

        assertThat(postLike2.getMember().getId()).isEqualTo(postLike.getMember().getId());
        assertThat(postLike2.getPost().getId()).isEqualTo(postLike.getPost().getId());

        // 4. 좋아요 등록(유니크 제약 조건 위배 예외 검증)
        assertThatThrownBy(() -> {
            em.persist(postLike2);
            em.flush();
        }).isInstanceOf(PersistenceException.class);
    }
}
