package com.medical.smartmedicine.common.util;

import com.medical.smartmedicine.common.enums.ResultCode;
import com.medical.smartmedicine.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 上下文工具类
 * 用于获取当前登录用户信息
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
public class SecurityContextUtil {

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     * @throws BusinessException 未登录时抛出异常
     */
    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("获取当前用户失败: 用户未登录");
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        try {
            // 从Authentication的principal中获取userId
            // JWT Filter中应该设置principal为userId
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof Integer) {
                return (Integer) principal;
            } else if (principal instanceof String) {
                return Integer.parseInt((String) principal);
            } else {
                log.error("无法解析principal类型: {}", principal.getClass());
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("获取当前用户ID失败", e);
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
    }

    /**
     * 获取当前登录用户ID (可选)
     * 如果未登录则返回null而不抛出异常
     *
     * @return 用户ID，未登录时返回null
     */
    public static Integer getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (BusinessException e) {
            return null;
        }
    }

    /**
     * 判断是否已登录
     *
     * @return true-已登录, false-未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
                && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }
}
