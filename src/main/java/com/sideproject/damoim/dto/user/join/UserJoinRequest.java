package com.sideproject.damoim.dto.user.join;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserJoinRequest {

    @Email(message = "이메일의 형식이 일치하지 않습니다.")
    @NotBlank(message = "이메일이 비어있습니다.")
    @Schema(description = "사용자 이메일", example = "qudrkq2237@test.com")
    private String email;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    @Schema(description = "사용자 비밀번호")
    private String password;

    @Schema(description = "사용자 비밀번호 재확인")
    private String passwordCheck;

    @NotBlank(message = "닉네임이 비어있습니다.")
    @Schema(description = "사용자 닉네임")
    private String nickname;
}
