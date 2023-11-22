package com.sideproject.damoim.dto.sProject.reply;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SProjectReplyEditRequest {

    @Schema(description = "댓글 idx")
    @Positive(message = "댓글 번호는 1 이상입니다.")
    private Long replyIdx;

    @Schema(description = "바뀐 댓글 내용")
    @NotBlank(message = "내용이 비어있습니다.")
    private String content;
}
