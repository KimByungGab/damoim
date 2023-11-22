package com.sideproject.damoim.controller;

import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.dto.sProject.volunteer.SProjectVolunteerListResponse;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.response.CommonResult;
import com.sideproject.damoim.response.ListResult;
import com.sideproject.damoim.service.ResponseService;
import com.sideproject.damoim.service.SProjectVolunteerService;
import com.sideproject.damoim.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "지원 모집", description = "지원 모집 관련 콘트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/SPost/volunteer")
@Validated
public class SProjectVolunteerController {

    private final ResponseService responseService;
    private final UserService userService;
    private final SProjectVolunteerService sProjectVolunteerService;

    // 현재 지원 상황 조회
    @GetMapping("/status")
    @Operation(summary = "지원 상태 확인", description = "현재 지원 상황을 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지원 상태 확인 성공"),
            @ApiResponse(responseCode = "404", description = "지원 상태 정보를 찾을 수 없음", content = @Content)
    })
    public ListResult<SProjectVolunteerListResponse> getCurrentRecruitGroupStatus(
            @Parameter(description = "게시글 idx", required = true, example = "1") @RequestParam @Positive(message = "게시글 번호는 1 이상입니다.") Long postIdx) {
        List<SProjectVolunteerListResponse> response = sProjectVolunteerService.getCurrentRecruitStatus(postIdx);
        if(response.size() == 0)
            throw new CContentNotFoundException();

        return responseService.getListResult(response);
    }

    // 그룹에 지원신청
    @PostMapping("/apply")
    @Operation(summary = "지원 신청", description = "모집 그룹에 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "지원 신청 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 지원신청함<br>모집 그룹의 게시글이 본인것임", content = @Content)
    })
    public ResponseEntity<CommonResult> applyVolunteer(
            Authentication auth,
            @Parameter(description = "그룹 idx", required = true, example = "1") @RequestParam @Positive(message = "지원그룹 번호는 1 이상입니다.") Long groupIdx) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectVolunteerService.addVolunteer(loginUser, groupIdx);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseService.getSuccessResult());
    }

    // 그룹에 지원취소
    @DeleteMapping("/cancel")
    @Operation(summary = "지원 취소", description = "신청했던 모집그룹을 신청취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지원 취소 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>지원하려는 그룹을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 처리됨", content = @Content)
    })
    public CommonResult cancelApplicationVolunteer(
            Authentication auth,
            @Parameter(description = "그룹 idx", required = true, example = "1") @RequestParam @Positive(message = "지원그룹 번호는 1 이상입니다.") Long groupIdx) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectVolunteerService.cancelApplicationVolunteerGroup(loginUser, groupIdx);

        return responseService.getSuccessResult();
    }

    @PatchMapping("/accept")
    @Operation(summary = "지원 승낙", description = "신청자의 지원을 승낙합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지원 승낙 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>지원하려는 그룹을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 처리됨", content = @Content)
    })
    public CommonResult acceptVolunteer(
            Authentication auth,
            @Parameter(description = "지원 식별번호", required = true, example = "1") @RequestParam @Positive(message = "지원 식별번호는 1 이상입니다.") Long volunteerIdx) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectVolunteerService.acceptVolunteer(loginUser, volunteerIdx);

        return responseService.getSuccessResult();
    }

    // 지원 거절
    @PatchMapping("/decline")
    @Operation(summary = "지원 거절", description = "신청자의 지원을 거절합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지원 거절 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>지원하려는 그룹을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 처리됨", content = @Content)
    })
    public CommonResult declineVolunteer(
            Authentication auth,
            @Parameter(description = "지원 식별번호", required = true, example = "1") @RequestParam @Positive(message = "지원 식별번호는 1 이상입니다.") Long volunteerIdx) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectVolunteerService.declineVolunteer(loginUser, volunteerIdx);

        return responseService.getSuccessResult();
    }
}
