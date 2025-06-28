package com.study.cook.api;

import com.study.cook.domain.EmailAuthStatus;
import com.study.cook.dto.EmailAuthResponse;
import com.study.cook.service.EmailAuthService;
import com.study.cook.util.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Slf4j
public class EmailAuthApiController {

    private final EmailAuthService emailAuthService;

    /**
     * 이메일 인증코드 전송
     * @param email
     * @return
     */
    @PostMapping("/code")
    public ResultVO sendAuthCode(@RequestParam String email) {
        EmailAuthResponse response = emailAuthService.sendAuthCode(email);
        return new ResultVO(response.getMessage(), "", response.getTtl(), true);
    }

    /**
     * 이메일 인증코드 검증
     * @param email
     * @param code
     * @return
     */
    @PostMapping("/verify")
    public ResultVO verifyCode(@RequestParam String email, @RequestParam String code) {
        EmailAuthStatus result = emailAuthService.verifyCode(email, code);

        switch (result) {
            case SUCCESS:
                return new ResultVO("이메일 인증이 완료되었습니다.", "", true);
            case CODE_MISMATCH:
                return new ResultVO("인증번호가 올바르지 않습니다.", "", false);
            case CODE_EXPIRED:
                return new ResultVO("인증 시간이 만료되었습니다. 다시 요청해주세요.", "", false);
            default:
                return new ResultVO("알 수 없는 오류가 발생했습니다.", "", false);
        }
    }
}
