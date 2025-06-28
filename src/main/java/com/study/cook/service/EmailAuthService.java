package com.study.cook.service;

import com.study.cook.domain.EmailAuthStatus;
import com.study.cook.dto.EmailAuthInfo;
import com.study.cook.dto.EmailAuthResponse;
import com.study.cook.exception.EmailAuthLimitException;
import com.study.cook.exception.EmailSendFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.study.cook.domain.EmailAuthSendResult.ALREADY_SENT;
import static com.study.cook.domain.EmailAuthSendResult.SUCCESS;
import static com.study.cook.domain.EmailAuthStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EmailAuthService {

    private final MailService mailService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(5);
    private static String formattedDuration = String.format("%d분", TTL.toMinutesPart());
    private static String emailAuthKey = "emailAuth:";
    private static String emailAuthLogKey = "emailAuth:log:";


    /**
     * 인증코드 이메일로 전송 및 Redis 저장
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public EmailAuthResponse sendAuthCode(String email) {
        String key = emailAuthKey + email;
        Long ttl = null;

        // 이미 인증된 이메일이면 재발송 막기
        if (isVerified(email).equals(EmailAuthStatus.SUCCESS)) {
            throw new EmailAuthLimitException("이미 인증된 이메일입니다.");
        }
        // 이미 인증코드가 Redis에 존재하면 재발송 막기
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
             ttl = getExpire(key);
            return new EmailAuthResponse(ALREADY_SENT, ttl, "이미 인증코드가 발급되었습니다. " + formattedDuration + " 후에 다시 시도해주세요.");
        }

        // 인증코드 생성
        String authCode = generateAuthCode();

        // Redis 저장(key: emailAuth:{email}, value: code, verified)
        EmailAuthInfo info = new EmailAuthInfo(authCode, false);
        redisTemplate.opsForValue().set(key, info, TTL);

        // 이메일 전송
        try {
            mailService.sendTextEmail(email, "[🍽️요리모여] 이메일 인증번호 안내"
                            ,"요리모여 인증번호: " + authCode + "\n\n" + formattedDuration + " 안에 인증해주세요.");
        } catch (MailException e) {
            log.error("이메일 전송 실패: {}, {}", email, e.getMessage(), e);
            // 예외 발생 시 롤백
            redisTemplate.delete(key);
            throw new EmailSendFailException("이메일 전송에 실패했습니다.: " + email);
        }

        ttl = getExpire(key);
        return new EmailAuthResponse(SUCCESS, ttl, "인증번호가 이메일로 전송되었습니다. \n인증코드를 입력해주세요!");
    }

    /**
     * redis 키 ttl 조회
     * @param key
     * @return
     */
    private Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 인증코드 검증
     */
    public EmailAuthStatus verifyCode(String email, String code) {
        String key = emailAuthKey + email;
        EmailAuthInfo info = (EmailAuthInfo) redisTemplate.opsForValue().get(key);

        if (info == null || info.getCode() == null) { // 인증코드 만료
            return CODE_EXPIRED;
        } else if (!info.getCode().equals(code)) { // 인증코드 불일치
            return CODE_MISMATCH;
        }

        info.setVerified(true);
        redisTemplate.opsForValue().set(key, info, Duration.ofMinutes(10));
        // 인증 시도 로그용
        redisTemplate.opsForValue().set("emailAuth:log:" + email, true, Duration.ofMinutes(30));
        return EmailAuthStatus.SUCCESS;
    }



    /**
     * 이메일 인증 여부
     */
    public EmailAuthStatus isVerified(String email) {
        String key = emailAuthKey + email;
        EmailAuthInfo info = (EmailAuthInfo) redisTemplate.opsForValue().get(key);
        Boolean hasTried = redisTemplate.hasKey("emailAuth:log:" + email);
        if (info != null && info.isVerified())
            return EmailAuthStatus.SUCCESS;
        if (info == null && Boolean.TRUE.equals(hasTried))
            return CODE_EXPIRED;

        return NO_AUTH_RECORD;
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
