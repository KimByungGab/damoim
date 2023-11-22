package com.sideproject.damoim.dto.sProject.reply;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SProjectReplyListResponse {

    @Schema(description = "댓글 idx")
    private Long idx;

    @Schema(description = "사용자 정보")
    private UserInfo userInfo;

    @Schema(description = "댓글 내용")
    private String content;

    @Schema(description = "부모 댓글 idx")
    private Long parentReplyIdx;

    @Schema(description = "작성일자")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class UserInfo {

        @Schema(description = "사용자 idx")
        private Long idx;

        @Schema(description = "사용자 닉네임")
        private String nickname;
    }
}
