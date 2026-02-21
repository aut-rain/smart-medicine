package com.medical.smartmedicine.common.config.security;

import com.medical.smartmedicine.common.constant.SecurityConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT令牌提供者
 * 负责JWT令牌的生成、解析和验证
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.issuer:smart-medicine}")
    private String issuer;

    /**
     * 生成Access Token
     *
     * @param userId 用户ID
     * @param userAccount 用户账号
     * @param role 用户角色
     * @return JWT Token
     */
    public String generateAccessToken(Integer userId, String userAccount, Integer role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(userAccount)
                .claim(SecurityConstant.CLAIM_USER_ID, userId)
                .claim(SecurityConstant.CLAIM_USER_ACCOUNT, userAccount)
                .claim(SecurityConstant.CLAIM_ROLE, role)
                .claim(SecurityConstant.CLAIM_TOKEN_TYPE, SecurityConstant.TOKEN_TYPE_ACCESS)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 生成Refresh Token
     *
     * @param userId 用户ID
     * @param userAccount 用户账号
     * @return Refresh Token
     */
    public String generateRefreshToken(Integer userId, String userAccount) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(userAccount)
                .claim(SecurityConstant.CLAIM_USER_ID, userId)
                .claim(SecurityConstant.CLAIM_TOKEN_TYPE, SecurityConstant.TOKEN_TYPE_REFRESH)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(SecurityConstant.CLAIM_USER_ID, Integer.class);
    }

    /**
     * 从Token中获取用户账号
     *
     * @param token JWT Token
     * @return 用户账号
     */
    public String getUserAccountFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户角色
     *
     * @param token JWT Token
     * @return 用户角色
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(SecurityConstant.CLAIM_ROLE, Integer.class);
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return true-有效, false-无效
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查Token是否过期
     *
     * @param token JWT Token
     * @return true-已过期, false-未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取Token的过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从Token中解析Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取Token剩余有效时间(毫秒)
     *
     * @param token JWT Token
     * @return 剩余有效时间
     */
    public long getRemainingValidity(String token) {
        Date expiration = getExpirationFromToken(token);
        return expiration.getTime() - System.currentTimeMillis();
    }
}
