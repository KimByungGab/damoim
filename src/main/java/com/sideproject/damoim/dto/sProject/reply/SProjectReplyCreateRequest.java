package com.sideproject.damoim.dto.sProject.reply;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SProjectReplyCreateRequest {

    @Schema(description = "게시글 idx")
    @Positive(message = "게시글 번호는 1 이상입니다.")
    private Long postIdx;

    @Schema(description = "게시글 내용")
    @NotBlank(message = "내용이 비어있습니다.")
    private String content;

    @Schema(description = "부모 댓글 idx")
    @Min(value = 0, message = "댓글 고유번호는 1 이상이며 없으면 0입니다.")
    private Long parentReplyIdx;
}
