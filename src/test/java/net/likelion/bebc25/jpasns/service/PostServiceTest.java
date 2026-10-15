package net.likelion.bebc25.jpasns.service;

import net.likelion.bebc25.jpasns.domain.Member;
import net.likelion.bebc25.jpasns.dto.CommentDto;
import net.likelion.bebc25.jpasns.dto.PostDto;
import net.likelion.bebc25.jpasns.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PostServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostService postService;

    private Long memberId;

    @BeforeEach
    void setUp(){
        Member member = new Member("haru@naver.com", "1234", "하루");
        memberRepository.save(member);
        memberId = member.getId();
    }

    @Test
    @DisplayName("게시글 생성 및 댓글 등록 후 상세 조회")
    void createPostAndComments(){
        PostDto.CreateRequest postRequest = new PostDto.CreateRequest("멍멍");
        PostDto.Response postResponse = postService.createPost(memberId, postRequest);

        postService.addComment(postResponse.id(), memberId, new CommentDto.CreateRequest("야옹"));
        postService.addComment(postResponse.id(), memberId, new CommentDto.CreateRequest("왈왈"));

        PostDto.DetailResponse response = postService.getPost(postResponse.id());

        assertThat(postResponse.content()).isEqualTo("멍멍");
        assertThat(postResponse.authorNickname()).isEqualTo("하루");
        assertThat(response.content()).isEqualTo("멍멍");
        assertThat(response.authorNickname()).isEqualTo("하루");
        assertThat(response.comments()).hasSize(2);
        assertThat(response.comments().getFirst().id()).isEqualTo(1);
        assertThat(response.comments().get(1).content()).isEqualTo("왈왈");
    }
}