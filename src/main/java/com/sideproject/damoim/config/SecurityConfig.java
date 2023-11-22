package com.sideproject.damoim.config;

import com.sideproject.damoim.filter.JwtTokenFilter;
import com.sideproject.damoim.security.JwtAccessDeniedHandler;
import com.sideproject.damoim.security.JwtAuthenticationEntryPoint;
import com.sideproject.damoim.jwt.JwtTokenProvider;
import com.sideproject.damoim.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtTokenFilter(userService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((ex) -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAccessDeniedHandler()))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/user/login",
                                "/user/join", "/", "/exception/**", "/user/info", "/user/refresh",
                                "/SPost/list", "/SPost/detail", "/SPost/reply", "/SPost/volunteer/status").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login.disable())
                .logout(logout -> logout.logoutUrl("/logout"));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/favicon.ico",
                "/error", "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/swagger-ui.html");
    }
}
