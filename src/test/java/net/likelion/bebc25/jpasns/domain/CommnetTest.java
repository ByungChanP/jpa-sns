package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CommnetTest {

    @Autowired
    private EntityManager em;

    private Long newMemberId;
    private Long newPostId;
    private Long newCommentId;

    @BeforeEach
    void setUp(){
        //1. 회원등록
        Member member = new Member("test1@test.com", "1234", "테스트1" );
        em.persist(member);
        this.newMemberId = member.getId();

        //2. 게시글 등록
        Post post = new Post(member, "테스트1 입니다.");
        em.persist(post);
        this.newPostId = post.getId();

        //3. 댓글 등록
        Comment comment = new Comment(member, "테스트1 댓글 등록 입니다.");
        comment.assignPost(post);
        em.persist(comment);
        this.newCommentId = comment.getId();

        em.clear(); // 캐시 삭제하여 캐시에서 가져오는게 아닌 DB에서 가져옴 -> 기존에는 없던 select 실행 -> 느려짐
    }

    @Test
    @DisplayName("댓글 생성")
    void create(){

        Post findPost = em.find(Post.class, newPostId);
        Member findMember = em.find(Member.class, newMemberId);

        // 댓글 등록
        Comment comment = new Comment(findMember, "테스트2 댓글 등록 입니다.");
        comment.assignPost(findPost);
        em.persist(comment);

        em.clear();

        // 등록한 댓글을 조회
        Comment findComment = em.find(Comment.class, comment.getId());

        // 조회한 댓글 정보를 이용해서 댓글 등록 검증
        assertThat(findComment).isNotNull();
        assertThat(findComment.getId()).isEqualTo(comment.getId());
        assertThat(findComment.getPost().getId()).isEqualTo(newPostId);
        assertThat(findComment.getMember().getNickname()).isEqualTo("테스트1");
        assertThat(findComment.getCreatedAt()).isNotNull();
    }
    @Test
    @DisplayName("댓글 조회")
    void read(){
        Comment findComment = em.find(Comment.class, newCommentId);

        assertThat(findComment).isNotNull();
        assertThat(findComment.getContent()).isEqualTo("테스트1 댓글 등록 입니다.");
        assertThat(findComment.getMember().getId()).isEqualTo(newMemberId);
        assertThat(findComment.getPost().getId()).isEqualTo(newPostId);

    }
    @Test
    @DisplayName("댓글 갱신")
    void update(){
        Comment findComment = em.find(Comment.class, newCommentId);

        // entity 속성이 수정되면 update 쿼리가 생성
        // 쿼리가 바로 실행되지 않고 버퍼에만 저장
        findComment.changeContent("수정된 테스트 1 댓글");
        // 버퍼에 저장한 쿼리는 commit 직전에 실행
        em.flush(); // 버퍼에 있는 메모리 일단 전송
        em.clear(); // 캐시 및 버퍼 삭제

        Comment updateComment = em.find(Comment.class, newCommentId);
        assertThat(updateComment.getContent()).isEqualTo("수정된 테스트 1 댓글");

    }
    @Test
    @DisplayName("댓글 삭제")
    void delete(){
        Comment targetComment = em.find(Comment.class, newCommentId);
        em.remove(targetComment);

        em.flush(); // 버퍼에 있는 메모리 일단 전송
        em.clear(); // 캐시 및 버퍼 삭제

        Comment findComment = em.find(Comment.class, newCommentId);
        assertThat(findComment).isNull();
    }

    @Test
    @DisplayName("게시글-댓글 양방향 연관관계 매핑")
    @Commit
    void postCommentBiDirectionalMapping() {
        Member member = em.find(Member.class, newMemberId);
        Post post = em.find(Post.class, newPostId);
        Comment comment2 = new Comment(member, "테스트2 댓글 등록 입니다.");
        Comment comment3 = new Comment(member, "테스트3 댓글 등록 입니다.");

        post.addComment(comment2);
        post.addComment(comment3);

        em.flush();
        em.clear();

        // 게시글을 조회시 댓글 목록 확인(역방향 1 -> N)
        Post findPost = em.find(Post.class, newPostId);
        assertThat(findPost).isNotNull();
        assertThat(findPost.getComments()).hasSize(3);
        assertThat(findPost.getComments())
                .extracting(Comment::getContent)
                .containsExactly("테스트1 댓글 등록 입니다.", "테스트2 댓글 등록 입니다.", "테스트3 댓글 등록 입니다.");

        // 댓글 조회시 게시글을 조회(정방향 N -> 1)
        Comment findComment = em.find(Comment.class, comment2.getId());
        assertThat(findComment).isNotNull();
        assertThat(findComment.getPost().getId()).isEqualTo(post.getId());
        assertThat(findComment.getPost().getMember().getNickname()).isEqualTo("테스트1");
        assertThat(findComment.getPost().getContent()).isEqualTo("테스트1 입니다.");
        assertThat(findComment.getPost().getMember().getEmail()).isEqualTo("test1@test.com");


    }
    @Test
    @DisplayName("Post를 통한 Comment 삭제")
    void postCommentBiDirectionalMappingDelete(){
        Post findPost = em.find(Post.class, newPostId);
        assertThat(findPost).isNotNull();
        assertThat(findPost.getComments()).hasSize(1);

        findPost.getComments().removeFirst();
        em.flush();
        em.clear();

        Post findPost2 = em.find(Post.class, findPost.getId());
        assertThat(findPost2).isNotNull();
        assertThat(findPost2.getComments()).hasSize(0);
    }
}
