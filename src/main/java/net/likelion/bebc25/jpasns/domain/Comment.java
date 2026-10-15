package net.likelion.bebc25.jpasns.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.likelion.bebc25.jpasns.common.entity.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 댓글 조회시 자동으로 게시글 참조
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false) // 외래키로 사용할 컬럼명
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500, columnDefinition = "TEXT")
    private String content;



    public Comment(Member member, String content) {
        this.member = member;
        this.content = content;

    }
    // 댓글과 연관되 게시글을 지정
    void assignPost(Post post){
        this.post = post;
    }
    // setter 얻는 상황{에서 댓글 수정에서 사용되는 비지니스 메서드
    public void changeContent(String content){
                this.content = content;
    }
}
