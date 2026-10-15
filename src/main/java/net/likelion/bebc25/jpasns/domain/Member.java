package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.likelion.bebc25.jpasns.common.entity.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //  기본생성자 생성
public class Member extends BaseTimeEntity {

    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 회원과 게시글의 1:N 관계를 정의하기 위한 리스트
    // 회원 검색만 하는데 굳이 다른거 까지 조회할 필요가 있을지는 고려(양방향 연관관계)
//    private List<Post> posts;

//    private List<PostLike> postLikes;

    public Member(String email, String password, String nickname) {
        this(email, password, nickname, Role.USER);
    }

    public Member(String email, String password, String nickname, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public void changeNickname(String nickname){
        this.nickname = nickname;

    }
}