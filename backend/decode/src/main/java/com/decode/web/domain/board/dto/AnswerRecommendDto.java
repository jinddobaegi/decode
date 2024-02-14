package com.decode.web.domain.board.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerRecommendDto {

    private Long id; // questionId
    private String content;
    private int recommendCnt;
}