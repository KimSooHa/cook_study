package com.study.cook.domain;

public enum EmailAuthSendResult {
    SUCCESS,             // 정상 전송
    ALREADY_VERIFIED,    // 이미 인증 완료됨
    ALREADY_SENT         // 인증코드 이미 발송됨 (ttl 반환 가능)
}