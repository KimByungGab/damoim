package com.sideproject.damoim.dto.sProject.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInfo {

    @Schema(description = "그룹명")
    @NotBlank(message = "그룹명이 비어있습니다.")
    private String name;

    @Schema(description = "모집 인원 수")
    @Min(value = 1, message = "모집인원은 1명 이상으로만 가능합니다.")
    private int recruitCount;
}
