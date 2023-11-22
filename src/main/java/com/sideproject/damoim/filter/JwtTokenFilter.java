package com.sideproject.damoim.filter;

import com.sideproject.damoim.entity.User;
import com.sideproject.damoim.jwt.JwtTokenProvider;
import com.sideproject.damoim.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(validateToken(authorizationHeader)) {
            // 토큰 값 추출
            String token = authorizationHeader.split(" ")[1];

            // JWT Token에서 userIdx 추출
            long userIdx = jwtTokenProvider.getUserIdx(token);

            // 유저 정보 호출
            User loginUser = userService.getUserByUserIdx(userIdx);

            // 유저 정보로 UsernamePasswordAuthenticationToken 발급
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginUser.getEmail(), null, List.of(new SimpleGrantedAuthority(loginUser.getRole().name())));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 권한 부여
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateToken(String authorizationHeader) {

        // Header의 Authorization이 비어있으면 JWT Token 전송하지 않아서 로그인 X
        if(authorizationHeader == null) {
            return false;
        }

        // Header의 Authorization 값이 "Bearer "로 시작하지 않으면 유효한 토큰이 아니므로 로그인 X
        if(!authorizationHeader.startsWith("Bearer ")) {
            return false;
        }

        // 토큰 값 추출
        String token = authorizationHeader.split(" ")[1];

        // 토큰이 만료되었으면 필터 진행시키지 않음. (인증 X)
        if(jwtTokenProvider.isExpired(token)) {
            return false;
        }

        return true;
    }
}
