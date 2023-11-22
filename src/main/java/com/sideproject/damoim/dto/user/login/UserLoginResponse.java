package com.sideproject.damoim.dto.user.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginResponse {

    @Schema(description = "인증 처리 시 Header 중 Authorization 값")
    private String accessToken;

    @Schema(description = "Access Token이 만료됐을 때 사용되는 토큰")
    private String refreshToken;
}
