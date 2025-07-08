package com.study.cook.api;

import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;


    @GetMapping("/valid-email")
    public Long emailCheck(@RequestParam("email") String email) {
        return memberService.countByEmail(email);
    }

    @GetMapping("/valid-loginId")
    public Long loginIdCheck(@RequestParam("loginId") String loginId, @RequestParam(required = false) Long memberId) {
        return memberService.countByLoginId(loginId, memberId);
    }

    @GetMapping("/valid-phoneNum")
    public Long phoneNumCheck(@RequestParam("phoneNum") String phoneNum) {
        return memberService.countByPhoneNum(phoneNum);
    }
}
