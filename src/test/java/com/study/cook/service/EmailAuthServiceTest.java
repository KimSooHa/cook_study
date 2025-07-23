package com.study.cook.service;

import com.study.cook.dto.EmailAuthInfo;
import com.study.cook.dto.EmailAuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class EmailAuthServiceTest {

    @Autowired
    private EmailAuthService emailAuthService;

    @MockBean
    private MailService mailService;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        // RedisTemplate mock 세팅
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }
    @Test
    void sendAuthCode_success() {
        // given
        String email = "test@example.com";
        String key = "emailAuth:" + email;
        when(redisTemplate.hasKey(key)).thenReturn(false);

        // when
        EmailAuthResponse response = emailAuthService.sendAuthCode(email);

        // then
        verify(mailService).sendTextEmail(eq(email), any(), contains("요리모여 인증번호")); // mailService.sendTextEmail(...)가 실제로 1번 이상 호출되었는지 확인
        verify(redisTemplate, atLeast(1)).opsForValue();
        verify(valueOperations).set(eq(key), any(EmailAuthInfo.class), any(Duration.class));

        assertThat(response.getMessage()).contains("인증번호가 이메일로 전송되었습니다");
    }

    @Test
    void verifyCode() {
    }

    @Test
    void isVerified() {
    }
}