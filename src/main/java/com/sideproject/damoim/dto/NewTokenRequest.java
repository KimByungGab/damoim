package com.sideproject.damoim.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewTokenRequest {

    @Schema(description = "'Bearer '를 뺀 나머지 Refresh Token")
    private String refreshToken;
}
