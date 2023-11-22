package com.sideproject.damoim.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String jwtKey;

    @Value("${jwt.accessTokenExpireTime}")
    private long accessTokenExpireTime;

    @Value("${jwt.refreshTokenExpireTime}")
    private long refreshTokenExpireTime;

    public String createAccessToken(long userIdx) {
        return createToken(userIdx, accessTokenExpireTime);
    }

    public String createRefreshToken(long userIdx) {
        return createToken(userIdx, refreshTokenExpireTime);
    }

    private String createToken(long userIdx, long expireTime) {

        Claims claims = Jwts.claims();
        claims.put("userIdx", userIdx);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS256, jwtKey)
                .compact();
    }

    public Long getUserIdx(String token) {
        try {
            return Long.parseLong(extractClaims(token).get("userIdx").toString());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isExpired(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token);

            return claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }
}
