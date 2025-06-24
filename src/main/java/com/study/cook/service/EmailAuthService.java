package com.study.cook.service;

import com.study.cook.domain.EmailAuthStatus;
import com.study.cook.dto.EmailAuthInfo;
import com.study.cook.exception.EmailSendFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EmailAuthService {

    private final MailService mailService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(5);
    private static String emailAuthKey = "emailAuth:";


    /**
     * 인증코드 이메일로 전송 및 Redis 저장
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public void sendAuthCode(String email) {
        String authCode = generateAuthCode();

        // Redis 저장(key: emailAuth:{email}, value: info)
        String key = emailAuthKey + email;
        EmailAuthInfo info = new EmailAuthInfo(authCode, false);
        redisTemplate.opsForValue().set(key, info, TTL);


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
