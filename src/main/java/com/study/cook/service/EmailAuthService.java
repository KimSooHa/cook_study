package com.study.cook.service;

import com.study.cook.domain.EmailAuth;
import com.study.cook.exception.EmailSendFailException;
import com.study.cook.repository.EmailAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EmailAuthService {

    private final EmailAuthRepository emailAuthRepository;
    private final MailService mailService;


    /**
     * 인증코드 이메일로 전송
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public void sendAuthCode(String email) {
        String authCode = generateAuthCode();

        // 기존 이메일 인증 정보가 있다면 삭제 or 갱신
        EmailAuth emailAuth = emailAuthRepository.findByEmail(email)
                .orElse(new EmailAuth(email));

        emailAuth.setAuthCode(authCode);
        emailAuth.setVerified(false);

        emailAuthRepository.save(emailAuth);

        // 이메일 전송
        try {
            mailService.sendTextEmail(email, "[🍽️요리모여] 이메일 인증번호 안내"
                    ,"요리모여 인증번호: " + authCode + "\n\n5분 안에 인증해주세요.");
        } catch (MailException e) {
            log.error("이메일 전송 실패: {}, {}", email, e.getMessage(), e);
            // 예외 발생 시 롤백
            throw new EmailSendFailException("이메일 전송에 실패했습니다.: " + email);
        }
    }

    /**
     * 인증코드 생성(6자리)
     */
    private String generateAuthCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}
