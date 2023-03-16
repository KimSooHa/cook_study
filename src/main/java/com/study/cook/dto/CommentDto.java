package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private Long commentId;
    private String content;
    private String regDateStr;
    private LocalDateTime regDate;
    private Long memberId;
    private String memberLoginId;

    @QueryProjection
    public CommentDto(Long commentId, String content, LocalDateTime regDate, Long memberId, String memberLoginId) {
        this.commentId = commentId;
        this.content = content;
        this.regDate = regDate;
        this.memberId = memberId;
        this.memberLoginId = memberLoginId;
    }
}
