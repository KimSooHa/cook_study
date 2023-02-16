package com.study.cook.api;

import com.study.cook.domain.Member;
import com.study.cook.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;


    @GetMapping("/valid-email")
    public Long emailCheck(@RequestParam("email") String email) {
        log.info("emailCnt={}", memberService.validDuplicateEmail(email));
        return memberService.validDuplicateEmail(email);
    }

    @GetMapping("/valid-loginId")
    public Long loginIdCheck(@RequestParam("loginId") String loginId) {
        log.info("loginIdCnt={}", memberService.validDuplicateLoginId(loginId));
        return memberService.validDuplicateLoginId(loginId);
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        // 엔티티 -> DTO 반환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

//    @PostMapping("/api/v2/members")
//    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
//        Member member = new Member();
//        member.setName(request.getName());
//
//        Long id = memberService.join(member);
//        return new CreateMemberResponse(id);
//    }
//
//    @PatchMapping("/api/v2/members/{id}")
//    public UpdateMemberResponse updateMemberV2(
//            @PathVariable("id") Long id,
//            @RequestBody @Valid UpdateMemberRequest request) {
//
//        memberService.update(id, request.getName());
//        Member findMember = memberService.findOne(id);
//        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
//    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }
    @Data
    static class CreateMemberResponse {

        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }

    }
    @Data
    static class UpdateMemberRequest {
        private String name;

    }
    @Data
    @AllArgsConstructor
    private class UpdateMemberResponse {
        private Long id;
        private String name;

    }
}
