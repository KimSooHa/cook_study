package com.study.cook.service;

import com.study.cook.dto.EmailAuthInfo;
import com.study.cook.dto.EmailAuthResponse;
import com.study.cook.enums.EmailAuthStatus;
import com.study.cook.exception.EmailAuthLimitException;
import com.study.cook.exception.EmailSendFailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static com.study.cook.enums.EmailAuthStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("이메일 인증 서비스 테스트")
class EmailAuthServiceTest {

    @Autowired
    private EmailAuthService emailAuthService;

    @MockBean
    private MailService mailService;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    private String email = "test@example.com";
    private String key = "emailAuth:" + email;
    private String code = "123456";

    @BeforeEach
    void setUp() {
        // RedisTemplate mock 세팅
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }
    @Test
    @DisplayName("인증코드 이메일로 전송 - 성공")
    void sendAuthCode_success() {
        // given
        when(redisTemplate.hasKey(key)).thenReturn(false);

        // when
        EmailAuthResponse response = emailAuthService.sendAuthCode(email);

        // then
        verify(mailService).sendTextEmail(eq(email), any(), contains("요리모여 인증번호")); // mailService.sendTextEmail(...)가 실제로 1번 이상 호출되었는지 확인
        verify(redisTemplate, atLeast(1)).opsForValue();
        // Redis에 인증코드 정보가 정상적으로 저장(set)되었는지 확인
        verify(valueOperations).set(eq(key), any(EmailAuthInfo.class), any(Duration.class));

        assertThat(response.getMessage()).contains("인증번호가 이메일로 전송되었습니다");
    }

    @Test
    @DisplayName("인증코드 이메일로 전송 - 이미 인증된 이메일")
    void sendAuthCode_fail_alreadyVerified() {
        // given
        EmailAuthInfo info = new EmailAuthInfo(code, true);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(info);
        when(redisTemplate.hasKey("emailAuth:log:" + email)).thenReturn(true); // 시도 로그 있어야만 SUCCESS 판별

        // when & then
        assertThrows(EmailAuthLimitException.class, () ->
            emailAuthService.sendAuthCode(email)
        );

        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(key);
    }

    @Test
    @DisplayName("인증코드 이메일로 전송 - 실패")
    void sendAuthCode_fail_emailSendException() {
        // given
        EmailAuthInfo info = new EmailAuthInfo(code, false);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        when(redisTemplate.hasKey(key)).thenReturn(false);
        when(redisTemplate.hasKey("emailAuth:log:" + email)).thenReturn(false);

        // 예외 강제 발생
        doThrow(new MailSendException("메일 전송 실패"))
                .when(mailService).sendTextEmail(anyString(), anyString(), anyString());

        // when & then
        // 예외발생 여부
        assertThrows(EmailSendFailException.class, () ->
            emailAuthService.sendAuthCode(email)
        );

        // 전송 실패 시 Redis에서 키 삭제됨
        verify(redisTemplate).delete(key);
        verify(mailService).sendTextEmail(any(), any(), any());
    }

    @Test
    @DisplayName("인증코드 검증 - 성공")
    void verifyCode_success() {
        // given
        EmailAuthInfo mockInfo = new EmailAuthInfo(code, false);
        when(valueOperations.get(key)).thenReturn(mockInfo);

        // when
        EmailAuthStatus status = emailAuthService.verifyCode(email, code);

        // then
        assertThat(status).isEqualTo(SUCCESS);
        verify(valueOperations).set(eq(key), argThat(info -> ((EmailAuthInfo) info).isVerified()), eq(Duration.ofMinutes(10))); // argThat : 검증된 상태로 변경되었는지 확인
        verify(valueOperations).set(eq("emailAuth:log:" + email), eq(true), eq(Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("인증코드 검증 - 실패")
    void verifyCode_failure() {
        // given
        EmailAuthInfo mockInfo = new EmailAuthInfo("654321", false);  // 다른 코드
        when(valueOperations.get(key)).thenReturn(mockInfo);

        // when
        EmailAuthStatus status = emailAuthService.verifyCode(email, code);

        // then
        assertThat(status).isEqualTo(CODE_MISMATCH);
    }
}