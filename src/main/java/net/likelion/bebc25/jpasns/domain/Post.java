package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.likelion.bebc25.jpasns.common.entity.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post", indexes = {
        @Index(name = "idx_post_member_id", columnList = "member_id")
})
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    // 객체관점에서의 설계
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //  조회의 성능 측면에서 권장되는 방식
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    private String imageUrl;

    @Column(nullable = false)
    private int likeCount;

    // 게시글 삭제시 댓글도 전부 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // 게시글 조회하면 자동으로 댓글 목록 조회


    // 이미지 주소가 없는경우
    public Post(Member member, String content) {
        this(member, content, null);
    }

    // 입력하여 전달 받는 데이터
    public Post(Member member, String content, String imageUrl) {
        this.member = member;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = 0;
    }

    // 양방향 객체 탐색이 가능하도록 지정하는 편의 메서드
    public void addComment(Comment comment){
        this.comments.add(comment);
        comment.assignPost(this);
    }
}
