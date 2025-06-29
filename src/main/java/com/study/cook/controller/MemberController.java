package com.study.cook.controller;

import com.study.cook.auth.CustomUserDetails;
import com.study.cook.domain.Member;
import com.study.cook.dto.LoginMember;
import com.study.cook.exception.CheckMatchPwdException;
import com.study.cook.exception.CheckNewPwdException;
import com.study.cook.exception.FindMemberException;
import com.study.cook.service.EmailAuthService;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final EmailAuthService emailAuthService;
    // 사용자 정보 기반으로 세션을 찾을 수 있도록 해주는 기능
    // 사용자의 로그인 ID (또는 username)를 key로 해서 세션을 조회할 수 있는 기능
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;


    @GetMapping
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "member/create-form";
    }

    @PostMapping
    public String create(Model model, @Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.info("errors={}", result);
            return "member/create-form";
        }

        // 이메일 인증 여부 체크
        switch (emailAuthService.isVerified(form.getEmail())) {
            case CODE_EXPIRED:
                model.addAttribute("msg", "인증코드가 만료되었습니다. 이메일 인증을 다시 해주세요.");
                model.addAttribute("url", "/members");
                return "member/create-form";
            case NO_AUTH_RECORD:
                model.addAttribute("msg", "이메일 인증이 완료되지 않았습니다.");
                model.addAttribute("url", "/members");
                return "member/create-form";
        }

        try {
            memberService.join(form);
        } catch (IllegalStateException e) {

            model.addAttribute("msg", e.getMessage());
            model.addAttribute("url", "/members");
            return "member/create-form";
        }

        log.info("회원가입 성공!");
        model.addAttribute("msg", "회원가입되었습니다!");
        model.addAttribute("url", "/");
        return "member/create-form";
    }

    @GetMapping("/{memberId}")
    public String update(@PathVariable Long memberId, Model model) {

        Member member = memberService.findOneById(memberId);

        MemberUpdateForm form = new MemberUpdateForm();
        form.setName(member.getName());
        form.setLoginId(member.getLoginId());
        form.setEmail(member.getEmail());
        form.setPhoneNum(member.getPhoneNum());

        model.addAttribute("memberForm", form);
        model.addAttribute("memberId", memberId);
        return "member/update-form";
    }

    @PutMapping("/{memberId}")
    public String update(@PathVariable Long memberId, @Valid MemberUpdateForm form, BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        if (result.hasErrors()) {
            // 💡 비밀번호 관련 필드 전부 비어 있으면 해당 에러 제거
            boolean isAllPwdFieldsEmpty =
                    StringUtils.hasText(form.getCurrentPwd()) == false &&
                            StringUtils.hasText(form.getNewPwd()) == false &&
                            StringUtils.hasText(form.getNewPwdConfirm()) == false;

            // 비밀번호 변경 안할 시 필드(currentPwd, newPwd, newPwdConfirm) 관련 에러 제외
            if (isAllPwdFieldsEmpty) {
                // 에러는 남아있지만 무시할 수 있도록, 직접 result.hasErrors() 대신 검증
                List<ObjectError> nonPwdErrors = result.getAllErrors().stream() // 유효성 검증에서 발생한 모든 에러리스트
                        .filter(e -> { // currentPwd, newPwd, newPwdConfirm 에러만 제외
                            if (e instanceof FieldError) { // 필드에 대한 에러인지 확인
                                String field = ((FieldError) e).getField();
                                return !field.equals("currentPwd") && !field.equals("newPwd") && !field.equals("newPwdConfirm");
                            }
                            return true; // Field 에러가 아니면 무시하지 말고 남겨둠
                        })
                        .collect(Collectors.toList()); // 최종적으로 남은 에러리스트들 리스트로 수집

                if (!nonPwdErrors.isEmpty()) {
                    return resetPwd(form, model);
                }

                // 비밀번호 외 모든 검증 통과한 경우: 통과!
            } else {
                log.info("errors={}", result);
                return resetPwd(form, model);
            }
        }

        // 이메일 인증 여부 체크
        if(memberService.countByEmail(form.getEmail()) < 1) {
           switch (emailAuthService.isVerified(form.getEmail())) {
                case CODE_EXPIRED:
                    model.addAttribute("emailError", "인증코드가 만료되었습니다. 이메일 인증을 다시 해주세요.");
                    model.addAttribute("memberForm", form);
                    return "member/update-form";
                case NO_AUTH_RECORD:
                    model.addAttribute("emailError", "이메일 인증이 완료되지 않았습니다.");
                    model.addAttribute("memberForm", form);
                    return "member/update-form";
            }
        }

        try {
            memberService.update(memberId, form);
        } catch (FindMemberException e) {
            return resetPwdAndAddMsg(form, model, "msg", e.getMessage());
        } catch (CheckMatchPwdException e) {
            return resetPwdAndAddMsg(form, model, "currentPwdError", e.getMessage());
        } catch (CheckNewPwdException e) {
            return resetPwdAndAddMsg(form, model, "newPwdError", e.getMessage());
        }

        // 세션 정보 갱신
        Member updatedMember = memberService.findOneById(memberId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails newPrincipal = new CustomUserDetails(updatedMember);
        CustomUserDetails newPrincipal = new CustomUserDetails(new LoginMember(updatedMember));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(newPrincipal, authentication.getCredentials(), authentication.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        // Redis 세션도 반영
        if (sessionRepository instanceof SessionRepository) {
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(false); // 세션이 없으면 null(false 때문에 새로 생성하지 않음)
            if (session != null) {
                session.setAttribute(
                        FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME,
                        updatedMember.getLoginId()
                );
            }
        }
        redirectAttributes.addFlashAttribute("msg", "수정하였습니다.");
        return "redirect:/mypage";
    }

    private static String resetPwdAndAddMsg(MemberUpdateForm form, Model model, String msg, String e) {
        model.addAttribute(msg, e);
        return resetPwd(form, model);
    }

    private static String resetPwd(MemberUpdateForm form, Model model) {
        form.setCurrentPwd("");
        form.setNewPwd("");
        form.setNewPwdConfirm("");
        model.addAttribute("memberForm", form);
        return "member/update-form";
    }

    // 탈퇴
    @DeleteMapping("/{memberId}")
    public String delete(@PathVariable Long memberId,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        // 현재 인증 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 인증 객체 제거 (세션 만료 + 시큐리티 컨텍스트 제거)
        if (auth != null) { // 현재 로그인된 세션 무효화
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        memberService.delete(memberId); // 회원 삭제

        redirectAttributes.addFlashAttribute("msg", "탈퇴되었습니다.");
        return "redirect:/";
    }
}
