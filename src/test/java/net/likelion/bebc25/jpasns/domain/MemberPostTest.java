package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberPostTest {
    @Autowired
    private EntityManager em;
    private Long newMemberId;

    @BeforeEach // 각 단위 테스트 실행하기 전에 먼저 실행되야하는 것들
    void setup(){
        // 회원 등록
         Member member = new Member("test1@test.com", "1234", "테스터 1");
         em.persist(member); // member insert

        newMemberId = member.getId();

        em.flush(); // 쿼리 강제 실행
        em.clear(); // 캐시 삭제 작업
    }

    @Test
    @DisplayName("회원 조회")
    void memberFind(){
        Member findMember = em.find(Member.class, newMemberId);
        assertThat(findMember).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo("test1@test.com");
        assertThat(findMember.getPassword()).isEqualTo("1234");
        assertThat(findMember.getRole()).isEqualTo(Role.USER);
        assertThat(findMember.getId()).isEqualTo(newMemberId);

    }

    @Test
    @DisplayName("게시글 등록 및 조회")
    void postSaveAndFind(){
        Member member = em.find(Member.class, newMemberId);
        Post post = new Post(member, "첫번째 게시글");
        em.persist(post);
        em.flush();
        em.clear();

        // 등록한 게시글 조회
        Post findPost = em.find(Post.class, post.getId());
        assertThat(findPost).isNotNull();
        assertThat(findPost.getContent()).isEqualTo("첫번째 게시글");
        assertThat(findPost.getMember().getId()).isEqualTo(newMemberId);
        assertThat(findPost.getMember().getNickname()).isEqualTo("테스터 1");
        assertThat(findPost.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("임베디드 주소 값 타입 맵핑 확인")
    void memberDetailAddressMapping(){
        Address address = new Address("12345", "경기도", "22-22");
        Member member = em.find(Member.class, newMemberId);
        MemberDetail memberDetail = new MemberDetail(member, address, "hi~");
        em.persist(memberDetail);

        em.flush();
        em.clear();

        MemberDetail findMember = em.find(MemberDetail.class, memberDetail.getId());

        assertThat(findMember).isNotNull();
        assertThat(findMember.getAddress().getZipcode()).isEqualTo("12345");
        assertThat(findMember.getAddress().getRoadAddress()).isEqualTo("경기도");
        assertThat(findMember.getAddress().getDetailAddress()).isEqualTo("22-22");

    }

    @Test
    @DisplayName("BaseTimeEntity 상속 동작 확인")
    void baseTimeAudition(){

        Member findMember = em.find(Member.class, newMemberId);

        LocalDateTime createdAt = findMember.getCreatedAt(); // 등록일
        LocalDateTime updatedAt = findMember.getCreatedAt(); // 수정일
        assertThat(createdAt).isNotNull(); // 등록일 주입 여부
        assertThat(updatedAt).isNotNull(); // 수정일 주입 여부
        assertThat(createdAt).isEqualTo(updatedAt); // 등록일과 수정일이 같은지 여부


        // 생성된 회원의 생성날짜와 수정 날짜가 작성되어 있는가?
        assertThat(findMember.getCreatedAt()).isNotNull();
        assertThat(findMember.getUpdatedAt()).isNotNull();

        findMember.changeNickname("테스터 2"); // update 쿼리 생성
        em.flush(); // 쿼리 실행
        em.clear(); // 캐시 제거

        Member updateMember = em.find(Member.class, newMemberId);

        assertThat(updateMember).isNotNull();
        assertThat(updateMember.getNickname()).isEqualTo("테스터 2"); // 수정된 닉네임 확인
        assertThat(updateMember.getCreatedAt()).isEqualTo(createdAt); // 등록시간은 수정되지 않음
        assertThat(updateMember.getUpdatedAt()).isNotEqualTo(updatedAt); // 수정일은 수정됨

    }

    @Test
    @DisplayName("회원 상세 정보 맵핑")
    void memberDetailMapping(){
        // 회원 조회
        Member member = em.find(Member.class, newMemberId);

        // 회원 상세정보 등록
        Address address = new Address("12345", "경기도", "고양동");
        MemberDetail memberDetail = new MemberDetail(member,address, "반가워용~");

        em.persist(memberDetail);
        em.flush();
        em.clear();

        // 상세정보를 제회한 회원 정보만 조회
        Member onlyMember = em.find(Member.class, newMemberId);
        assertThat(onlyMember).isNotNull();
        assertThat(onlyMember.getNickname()).isEqualTo("테스터 1");


        // 상세정보를 포함한 회원 정보 조회
        MemberDetail withMemberDetail = em.find(MemberDetail.class, newMemberId);
        assertThat(withMemberDetail).isNotNull();
        assertThat(withMemberDetail.getMember().getNickname()).isEqualTo("테스터 1");
        assertThat(withMemberDetail.getIntroduction()).isEqualTo("반가워용~");
        assertThat(withMemberDetail.getAddress().getDetailAddress()).isEqualTo("고양동");
    }

    @Test
    @DisplayName("게시글-태그 연결 엔티티 등록 및 다대일 객체 탐색 검증")
    void postHashtagSaveAndGraphTraversal() {
        // 1. 작성자 회원 및 게시글 생성
        Member member = em.find(Member.class, newMemberId);
        Post post = new Post(member, "해시태그 테스트 게시글");
        em.persist(post);

        // 2. 독립 Tag 엔티티 생성 및 영속화
        Tag springTag = new Tag("SpringBoot");
        Tag jpaTag = new Tag("JPA");
        em.persist(springTag);
        em.persist(jpaTag);

        // 3. 다대다 연결 엔티티(PostHashtag) 생성 및 영속화
        PostHashtag postHashtag1 = new PostHashtag(post, springTag);
        PostHashtag postHashtag2 = new PostHashtag(post, jpaTag);
        em.persist(postHashtag1);
        em.persist(postHashtag2);

        em.flush();
        em.clear();

        // 4. 연결 엔티티 조회를 통한 다대일 단방향 객체 탐색 검증
        PostHashtag findHashtag = em.find(PostHashtag.class, postHashtag1.getId());
        assertThat(findHashtag).isNotNull();
        assertThat(findHashtag.getPost().getId()).isEqualTo(post.getId());
        assertThat(findHashtag.getPost().getContent()).isEqualTo("해시태그 테스트 게시글");
        assertThat(findHashtag.getTag().getId()).isEqualTo(springTag.getId());
        assertThat(findHashtag.getTag().getName()).isEqualTo("SpringBoot");
    }

    @Test
    @DisplayName("동일 게시글에 동일 태그 중복 연결 시 복합 유니크 제약조건 위반 검증")
    void duplicatePostHashtagThrowsException() {
        // 1. 게시글 및 태그 생성
        Member member = em.find(Member.class, newMemberId);
        Post post = new Post(member, "중복 태그 테스트 게시글");
        em.persist(post);

        Tag tag = new Tag("Java");
        em.persist(tag);

        // 2. 첫 번째 해시태그 연결 등록
        PostHashtag firstHashtag = new PostHashtag(post, tag);
        em.persist(firstHashtag);
        em.flush();

        // 3. 동일한 게시글에 동일한 태그로 두 번째 연결 등록 시도 시 예외 발생 검증
        PostHashtag duplicateHashtag = new PostHashtag(post, tag);

        // IDENTITY 전략은 PK 채번을 위해 em.persist() 호출 즉시 INSERT 쿼리가 실행되므로 persist 시점에 복합 유니크 위반 예외 발생
        assertThatThrownBy(() -> {
            em.persist(duplicateHashtag);
            em.flush();
        }).isInstanceOf(Exception.class);
    }
}
