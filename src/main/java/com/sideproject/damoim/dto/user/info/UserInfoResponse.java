package com.sideproject.damoim.dto.user.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoResponse {

    @Schema(description = "사용자 이메일")
    private String email;

    @Schema(description = "사용자 닉네임")
    private String nickname;

    @Schema(description = "사용자 역할. USER, ADMIN으로 나뉜다.")
    private String role;
}
