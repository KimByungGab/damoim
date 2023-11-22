package com.sideproject.damoim.dto.user.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequest {

    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "사용자 비밀번호")
    private String password;
}
