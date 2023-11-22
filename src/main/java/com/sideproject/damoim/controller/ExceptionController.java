package com.sideproject.damoim.controller;

import com.sideproject.damoim.advice.exception.CAccessDeniedException;
import com.sideproject.damoim.advice.exception.CAuthenticationEntryPointException;
import com.sideproject.damoim.response.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "예외 처리 관련", description = "예외 처리 관련 리다이렉트 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/exception")
public class ExceptionController {

    // 인증 오류 시
    @GetMapping("/entrypoint")
    @Operation(summary = "인증 예외처리", description = "인증 오류에 대한 리다이렉트입니다.")
    @ApiResponse(responseCode = "401", description = "인증 오류", content = @Content)
    public CommonResult entrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    // 권한 오류 시
    @GetMapping("/accessdenied")
    @Operation(summary = "접근 예외처리", description = "접근 권한이 없을 떄의 리다이렉트입니다.")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content)
    public CommonResult accessdeniedException() {
        throw new CAccessDeniedException();
    }
}
