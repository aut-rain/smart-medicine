package com.medical.smartmedicine.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /**
     * 普通用户
     */
    USER(0, "ROLE_USER", "普通用户"),

    /**
     * 管理员
     */
    ADMIN(1, "ROLE_ADMIN", "管理员");

    /**
     * 角色状态值
     */
    private final Integer status;

    /**
     * Spring Security角色名称
     */
    private final String roleName;

    /**
     * 角色描述
     */
    private final String description;

    /**
     * 根据状态值获取角色枚举
     *
     * @param status 状态值
     * @return 角色枚举
     */
    public static RoleEnum fromStatus(Integer status) {
        if (status == null) {
            return USER;
        }
        for (RoleEnum role : values()) {
            if (role.status.equals(status)) {
                return role;
            }
        }
        return USER;
    }

    /**
     * 根据角色名称获取角色枚举
     *
     * @param roleName 角色名称
     * @return 角色枚举
     */
    public static RoleEnum fromRoleName(String roleName) {
        for (RoleEnum role : values()) {
            if (role.roleName.equals(roleName)) {
                return role;
            }
        }
        return USER;
    }
}
