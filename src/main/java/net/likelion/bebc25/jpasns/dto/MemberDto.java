package net.likelion.bebc25.jpasns.dto;

import net.likelion.bebc25.jpasns.domain.Member;
import net.likelion.bebc25.jpasns.domain.Role;

public class MemberDto {

    // 회원 정보를 담는 요청 DTO
    public record CreateRequest(
            String email,
            String password,
            String nickname
    ) {
        public Member toEntity() {
            return new Member(email, password, nickname, Role.USER);
        }
    }

    // 회원 정보를 담는 응답 DTO
    public record Response(
            Long id,
            String email,
            String nickname,
            String role
    ) {
        // 트랜잭션 내부에서 엔티티를 DTO로 변환하는 정적 팩토리 메서드
        public static Response from(Member member) {
            return new Response(
                    member.getId(),
                    member.getEmail(),
                    member.getNickname(),
                    member.getRole().name()
            );
        }
    }
}