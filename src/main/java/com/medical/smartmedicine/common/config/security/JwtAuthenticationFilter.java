package com.medical.smartmedicine.common.config.security;

import com.medical.smartmedicine.common.constant.RedisConstant;
import com.medical.smartmedicine.common.constant.SecurityConstant;
import com.medical.smartmedicine.common.util.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 拦截所有请求，验证JWT Token，并设置Security上下文
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. 从请求头获取Token
            String token = getTokenFromRequest(request);

            // 2. 验证Token
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 3. 检查Token是否在黑名单中
                if (isTokenBlacklisted(token)) {
                    log.debug("Token已失效(在黑名单中)");
                    filterChain.doFilter(request, response);
                    return;
                }

                // 4. 从Token中提取用户信息
                Integer userId = jwtTokenProvider.getUserIdFromToken(token);
                String userAccount = jwtTokenProvider.getUserAccountFromToken(token);
                Integer role = jwtTokenProvider.getRoleFromToken(token);

                // 5. 设置UserContextHolder (ThreadLocal)
                UserContextHolder.setUserId(userId);

                // 6. 创建Authentication对象并设置到Security上下文
                // 使用userId作为principal，便于后续获取
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId, // principal设置为userId
                                null, // credentials
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + getRoleName(role)))
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT认证成功: userId={}, userAccount={}, role={}", userId, userAccount, role);
            }
        } catch (Exception e) {
            log.error("JWT认证过程发生异常", e);
            // 发生异常时清理上下文
            SecurityContextHolder.clearContext();
            UserContextHolder.clear();
        }

        // 7. 继续过滤器链
        filterChain.doFilter(request, response);

        // 8. 请求结束后清理ThreadLocal，防止内存泄漏
        UserContextHolder.clear();
    }

    /**
     * 从请求头中提取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstant.TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            return bearerToken.substring(SecurityConstant.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 检查Token是否在黑名单中
     */
    private boolean isTokenBlacklisted(String token) {
        String key = RedisConstant.TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 根据角色码获取角色名称
     */
    private String getRoleName(Integer roleStatus) {
        if (roleStatus == null) {
            return "USER";
        }
        return switch (roleStatus) {
            case 1 -> "ADMIN";
            case 0 -> "USER";
            default -> "USER";
        };
    }
}
