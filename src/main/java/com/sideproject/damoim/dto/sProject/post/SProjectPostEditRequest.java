package com.sideproject.damoim.dto.sProject.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SProjectPostEditRequest {

    @Schema(description = "게시글 정보")
    private PostInfo postInfo;

    @Schema(description = "그룹 정보 리스트")
    private List<GroupInfo> groupInfos;

    @Getter
    @Setter
    public static class PostInfo {

        @Schema(description = "게시글 idx")
        @Min(value = 1, message = "게시글 번호는 1 이상입니다.")
        private Long idx;

        @Schema(description = "바뀐 게시글 제목")
        @NotBlank(message = "제목이 비어있습니다.")
        private String title;

        @Schema(description = "바뀐 게시글 내용")
        @NotBlank(message = "내용이 비어있습니다.")
        private String content;
    }
}
