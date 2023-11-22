package com.sideproject.damoim.dto.sProject.volunteer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SProjectVolunteerListResponse {

    @Schema(description = "지원 그룹 idx")
    private Long idx;

    @Schema(description = "지원 그룹명")
    private String name;

    @Schema(description = "현재 지원 수")
    private int currentCount;

    @Schema(description = "총 모집 수")
    private int totalCount;
}
