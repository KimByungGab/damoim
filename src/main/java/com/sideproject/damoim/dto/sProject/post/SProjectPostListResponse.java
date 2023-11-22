package com.sideproject.damoim.dto.sProject.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SProjectPostListResponse {

    @Schema(description = "게시글 idx")
    private Long idx;

    @Schema(description = "사용자 정보")
    private UserInfo userInfo;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 작성일자")
    private LocalDateTime createdAt;

    @Schema(description = "모집 여부")
    private boolean opened;

    @Getter
    @Setter
    public static class UserInfo {

        @Schema(description = "사용자 idx")
        private Long idx;

        @Schema(description = "사용자 이메일")
        private String email;

        @Schema(description = "사용자 닉네임")
        private String nickname;
    }
}
