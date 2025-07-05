package com.study.cook.enums;

public enum EmailAuthStatus {
    SUCCESS, // 인증 성공
    CODE_EXPIRED, // 인증코드 만료
    CODE_MISMATCH, // 인증코드 불일치
    NO_AUTH_RECORD  // 인증 시도조차 없음
}