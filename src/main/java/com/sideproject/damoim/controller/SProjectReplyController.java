package com.sideproject.damoim.controller;

import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.dto.sProject.reply.SProjectReplyCreateRequest;
import com.sideproject.damoim.dto.sProject.reply.SProjectReplyEditRequest;
import com.sideproject.damoim.dto.sProject.reply.SProjectReplyListResponse;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.response.CommonResult;
import com.sideproject.damoim.response.ListResult;
import com.sideproject.damoim.service.ResponseService;
import com.sideproject.damoim.service.SProjectReplyService;
import com.sideproject.damoim.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시글 댓글", description = "게시글의 댓글 관련 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/SPost/reply")
@Validated
public class SProjectReplyController {

    private final UserService userService;
    private final ResponseService responseService;
    private final SProjectReplyService sProjectReplyService;

    // 사이드 프로젝트 게시글 댓글 작성
    @PostMapping("/create")
    @Operation(summary = "댓글 작성", description = "원하는 게시글에 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>게시글을 찾을 수 없음<br>부모 댓글을 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<CommonResult> createSProjectReply(Authentication auth, @Valid @RequestBody SProjectReplyCreateRequest request) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectReplyService.createSProjectReply(loginUser, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseService.getSuccessResult());
    }

    // 댓글 리스트 조회
    @GetMapping()
    @Operation(summary = "댓글 리스트 조회", description = "원하는 게시글의 댓글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content)
    })
    public ListResult<SProjectReplyListResponse> getSProjectReply(
            @Parameter(description = "게시글 idx", required = true, example = "1") @RequestParam @Positive(message = "게시글 번호는 1 이상입니다.") Long postIdx,
            @Parameter(description = "부모 댓글 idx", required = true, example = "1") @RequestParam @Min(value = 0, message = "대댓글의 대상 댓글 번호는 0 이상입니다.") Long parentReplyIdx,
            @Parameter(description = "페이지", required = true, example = "1") @RequestParam @Positive(message = "페이지는 1 이상부터 가능합니다.") int page,
            @Parameter(description = "페이지 당 정보 개수", required = true, example = "1") @RequestParam @Positive(message = "정보 개수는 1개 이상부터 가능합니다.") int pageSize) {
        List<SProjectReplyListResponse> responses = sProjectReplyService.getSProjectReplyList(postIdx, parentReplyIdx, page, pageSize);
        if(responses.size() == 0)
            throw new CContentNotFoundException();

        return responseService.getListResult(responses);
    }

    // 댓글 수정
    @PatchMapping("/edit")
    @Operation(summary = "댓글 수정", description = "원하는 게시글의 댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>게시글을 찾을 수 없음", content = @Content)
    })
    public CommonResult editSProjectReply(Authentication auth,
                                          @Valid @RequestBody SProjectReplyEditRequest request) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectReplyService.updateSProjectReply(request.getReplyIdx(), loginUser.getIdx(), request.getContent());

        return responseService.getSuccessResult();
    }

    // 댓글 내용 삭제
    @DeleteMapping("/delete")
    @Operation(summary = "댓글 삭제", description = "원하는 게시글의 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 완료"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>게시글을 찾을 수 없음", content = @Content)
    })
    public CommonResult deleteSProjectReply(
            Authentication auth,
            @Parameter(description = "댓글 idx", required = true, example = "1") @RequestParam @Positive(message = "댓글 번호는 1 이상입니다.") Long replyIdx) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectReplyService.deleteSProjectReply(replyIdx, loginUser.getIdx());

        return responseService.getSuccessResult();
    }
}
