package net.likelion.bebc25.jpasns.controller;


import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.jpasns.dto.MemberDto;
import net.likelion.bebc25.jpasns.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 가입
    @PostMapping
    public ResponseEntity<MemberDto.Response> signUp(@RequestBody MemberDto.CreateRequest request){
        MemberDto.Response response = memberService.signUp(request);
        return ResponseEntity.created(URI.create("api/v1/members/" + response.id())).body(response);
    }
    // 회원 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<MemberDto.Response> getMember(@PathVariable Long id){
        MemberDto.Response response = memberService.getMember(id);
        return ResponseEntity.ok(response); // 응답 상태 코드 200
    }
}
