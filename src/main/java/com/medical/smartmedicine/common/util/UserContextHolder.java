package com.medical.smartmedicine.common.util;

import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户上下文工具类
 * 用于在ThreadLocal中存储和获取当前登录用户信息
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
public class UserContextHolder {

    private static final ThreadLocal<Integer> USER_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(Integer userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     * @throws BusinessException 未登录时抛出异常
     */
    public static Integer getUserId() {
        Integer userId = USER_ID_HOLDER.get();
        if (userId == null) {
            log.warn("获取当前用户失败: 用户未登录");
            throw new BusinessException(ErrorCodeEnum.UNAUTHORIZED);
        }
        return userId;
    }

    /**
     * 获取当前用户ID (可选)
     * 如果未登录则返回null而不抛出异常
     *
     * @return 用户ID，未登录时返回null
     */
    public static Integer getUserIdOrNull() {
        return USER_ID_HOLDER.get();
    }

    /**
     * 判断是否已登录
     *
     * @return true-已登录, false-未登录
     */
    public static boolean isAuthenticated() {
        return USER_ID_HOLDER.get() != null;
    }

    /**
     * 清除当前用户信息
     * 应该在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
    }
}
