package com.example.last.controller;

import com.example.last.dto.ChangePasswordRequestDto;
import com.example.last.dto.MemberRequestDto;
import com.example.last.dto.MemberResponseDto;
import com.example.last.service.MemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;


    // 토큰 기반 계정 조회
    @GetMapping("/me")
    @ApiOperation(value = "토큰 기반 계정 조회", notes = "해당 토큰을 갖는 계정을 조회하는 API입니다.")
    public ResponseEntity<MemberResponseDto> getMyMemberInfo() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        return ResponseEntity.ok((myInfoBySecurity));
    }

    // 닉네임 변경
    @PostMapping("/nickname")
    @ApiOperation(value = "닉네임 변경", notes = "닉네임을 변경하는 API입니다.")
    public ResponseEntity<MemberResponseDto> setMemberNickname(@RequestBody MemberRequestDto request) {
        return ResponseEntity.ok(memberService.changeMemberNickname(request.getEmail(), request.getNickname()));
    }

    // 비밀번호 변경
    @PostMapping("/password")
    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호를 변경하는 API입니다.")
    public ResponseEntity<MemberResponseDto> setMemberPassword(@RequestBody ChangePasswordRequestDto request) {
        return ResponseEntity.ok(memberService.changeMemberPassword(request.getEmail(), request.getExPassword(), request.getNewPassword()));
    }

}
