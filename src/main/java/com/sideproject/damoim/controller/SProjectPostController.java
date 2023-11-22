package com.sideproject.damoim.controller;

import com.sideproject.damoim.advice.exception.CContentNotFoundException;
import com.sideproject.damoim.dto.sProject.post.SProjectPostCreateRequest;
import com.sideproject.damoim.dto.sProject.post.SProjectPostEditRequest;
import com.sideproject.damoim.dto.sProject.post.SProjectPostInfoResponse;
import com.sideproject.damoim.dto.sProject.post.SProjectPostListResponse;
import com.sideproject.damoim.entity.SProjectPost;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.response.CommonResult;
import com.sideproject.damoim.response.ListResult;
import com.sideproject.damoim.service.ResponseService;
import com.sideproject.damoim.service.SProjectPostService;
import com.sideproject.damoim.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시글", description = "게시글 관련 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/SPost")
@Validated
public class SProjectPostController {

    private final ResponseService responseService;
    private final SProjectPostService sProjectPostService;
    private final UserService userService;

    // 게시글 리스트 조회
    @GetMapping("/list")
    @Operation(summary = "게시글 리스트 조회", description = "게시글 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content)
    })
    public ListResult<SProjectPostListResponse> getSProjectPostList(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String searchKeyword,
            @Parameter(description = "페이지", required = true, example = "1") @RequestParam @Positive(message = "페이지는 1 이상부터 가능합니다.") int page,
            @Parameter(description = "페이지 당 정보 개수", required = true, example = "1") @RequestParam @Positive(message = "정보 개수는 1개 이상부터 가능합니다.") int pageSize) {
        List<SProjectPostListResponse> response = sProjectPostService.getSProjectPostList(searchKeyword, page, pageSize);

        if(response.size() == 0)
            throw new CContentNotFoundException();

        return responseService.getListResult(response);
    }

    // 게시글 작성
    @PostMapping("/create")
    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<CommonResult> createSProjectPost(Authentication auth, @Valid @RequestBody SProjectPostCreateRequest request) {
        User loginUser = userService.getLoginUserInfo(auth);

        SProjectPost newPost = sProjectPostService.createSProjectPost(loginUser, request.getPostInfo());
        sProjectPostService.createSProjectRecruitGroup(newPost, request.getGroupInfos());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseService.getSuccessResult());
    }

    // 게시글 상세보기
    @GetMapping("/detail")
    @Operation(summary = "게시글 상세보기", description = "게시글을 상세하게 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content)
    })
    public CommonResult getSProjectPostInfo(
            @Parameter(description = "게시글 idx", required = true, example = "1") @RequestParam @Positive(message = "게시글 번호는 1 이상입니다.") Long idx) {
        SProjectPostInfoResponse response = sProjectPostService.getSProjectPostInfo(idx);
        if(response == null)
            throw new CContentNotFoundException();

        return responseService.getSingleResult(response);
    }

    // 게시글 수정
    @PatchMapping("/edit")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>게시글을 찾을 수 없음", content = @Content)
    })
    public CommonResult editSProjectPost(Authentication auth, @Valid @RequestBody SProjectPostEditRequest request) {
        User loginUser = userService.getLoginUserInfo(auth);

        SProjectPost editedPost = sProjectPostService.editSProjectPost(loginUser, request.getPostInfo());
        sProjectPostService.deleteSProjectRecruitGroup(loginUser, editedPost);
        sProjectPostService.createSProjectRecruitGroup(editedPost, request.getGroupInfos());

        return responseService.getSuccessResult();
    }

    // 게시글 삭제
    @DeleteMapping("/delete")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "유저 인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음<br>게시글을 찾을 수 없음", content = @Content)
    })
    public CommonResult deleteSProjectPost(
            Authentication auth,
            @Parameter(description = "게시글 idx", required = true, example = "1") @RequestParam @Positive(message = "게시글 번호는 1 이상입니다.") Long idx) {
        User loginUser = userService.getLoginUserInfo(auth);

        sProjectPostService.deleteSProjectPost(loginUser, idx);

        return responseService.getSuccessResult();
    }
}