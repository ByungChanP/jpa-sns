package net.likelion.bebc25.jpasns.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.jpasns.domain.Member;
import net.likelion.bebc25.jpasns.dto.MemberDto;
import net.likelion.bebc25.jpasns.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // final 필드용 생성자 자동 생성
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional // 쓰기 전용
    public MemberDto.Response signUp(MemberDto.CreateRequest request){
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("사용중인 이메일입니다. email: " + request.email());
        }
        Member member = request.toEntity();
        Member savedMember = memberRepository.save(member);
        return MemberDto.Response.from(savedMember);
    }


    // 회원 조회
    public MemberDto.Response getMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 회원입니다."));
        return  MemberDto.Response.from(member);
    }
}
