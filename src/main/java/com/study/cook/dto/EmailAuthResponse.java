package com.study.cook.dto;

import com.study.cook.domain.EmailAuthSendResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailAuthResponse {
    private EmailAuthSendResult status;
    private Long ttl;         // 초 단위 TTL (null일 수도)
    private String message;
    
    // constructor, getter
}