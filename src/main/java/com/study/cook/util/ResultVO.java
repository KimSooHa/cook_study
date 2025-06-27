package com.study.cook.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class ResultVO {

    private String msg;
    private String url;
    private Long ttl;
    private boolean success;

    public ResultVO(String msg, String url, boolean success) {
        this.msg = msg;
        this.url = url;
        this.success = success;
    }

    public ResultVO(String msg, String url, Long ttl, boolean success) {
        this.msg = msg;
        this.url = url;
        this.ttl = ttl;
        this.success = success;
    }

}
