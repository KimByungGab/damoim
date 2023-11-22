package com.sideproject.damoim.controller;

import com.sideproject.damoim.advice.exception.*;
import com.sideproject.damoim.dto.*;
import com.sideproject.damoim.dto.user.info.UserInfoResponse;
import com.sideproject.damoim.dto.user.join.UserJoinRequest;
import com.sideproject.damoim.dto.user.login.UserLoginRequest;
import com.sideproject.damoim.dto.user.login.UserLoginResponse;
import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.jwt.JwtTokenProvider;
import com.sideproject.damoim.response.CommonResult;
import com.sideproject.damoim.response.SingleResult;
import com.sideproject.damoim.service.ResponseService;
import com.sideproject.damoim.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저", description = "유저 관련 컨트롤러입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join")
    @Operation(summary = "회원 가입", description = "새로운 회원 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 성공"),
            @ApiResponse(responseCode = "409", description = "Request 유효 검사 통과 실패", content = @Content)
    })
    public ResponseEntity<CommonResult> join(@Valid @RequestBody UserJoinRequest userJoinRequest) {

        if(userService.checkEmailDuplicate(userJoinRequest.getEmail()))
            throw new CDuplicateEmailException();
        if(userService.checkNicknameDuplicate(userJoinRequest.getNickname()))
            throw new CDuplicateNicknameException();
        if(!userJoinRequest.getPassword().equals(userJoinRequest.getPasswordCheck()))
            throw new CNotSamePasswordException();

        userService.joinUser(userJoinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseService.getSuccessResult());
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content)
    })
    public SingleResult<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {

        User user = userService.login(userLoginRequest);

        if(user == null)
            throw new CLoginFailException();

        UserLoginResponse response = new UserLoginResponse();
        response.setAccessToken(jwtTokenProvider.createAccessToken(user.getIdx()));
        response.setRefreshToken(jwtTokenProvider.createRefreshToken(user.getIdx()));

        return responseService.getSingleResult(response);
    }

    @GetMapping("/info")
    @Operation(summary = "사용자 정보", description = "사용자의 정보 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 호출 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = @Content)
    })
    public SingleResult<UserInfoResponse> getUserInfo(
            Authentication auth,
            @Parameter(description = "사용자 닉네임", required = true) @RequestParam String nickname) {

        User loginUser = userService.getUserByNickname(nickname);

        if(loginUser == null)
            throw new CUserNotFoundException();

        if(auth != null && auth.getName().equals(loginUser.getEmail())) {
            // 본인 계정인 경우
        } else {
            // 다른 사람의 계정인 경우
        }

        UserInfoResponse res = new UserInfoResponse();
        res.setEmail(loginUser.getEmail());
        res.setNickname(loginUser.getNickname());
        res.setRole(loginUser.getRole().name());

        return responseService.getSingleResult(res);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh 토큰으로 토큰 전체 갱신")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = @Content)
    })
    public SingleResult<UserLoginResponse> getNewToken(@RequestBody NewTokenRequest newTokenRequest) {
        Long userIdx = jwtTokenProvider.getUserIdx(newTokenRequest.getRefreshToken());

        if(userIdx == null)
            throw new CAuthenticationEntryPointException();

        User user = userService.getUserByUserIdx(userIdx);
        if(user == null)
            throw new CUserNotFoundException();

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setAccessToken(jwtTokenProvider.createAccessToken(userIdx));
        userLoginResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(userIdx));

        return responseService.getSingleResult(userLoginResponse);
    }
}
