package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private String content;
    private LocalDateTime regDate;
    private String nickname;

    @QueryProjection
    public CommentDto(String content, LocalDateTime regDate, String nickname) {
        this.content = content;
        this.regDate = regDate;
        this.nickname = nickname;
    }
}
