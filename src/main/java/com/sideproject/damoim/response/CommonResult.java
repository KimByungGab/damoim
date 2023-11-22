package com.sideproject.damoim.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {

    @Schema(description = "응답 성공여부")
    private boolean success;

    @Schema(description = "응답 코드번호")
    private int code;

    @Schema(description = "응답 메시지")
    private String msg;
}
