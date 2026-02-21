package com.medical.smartmedicine.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户信息更新DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息更新请求")
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户姓名
     */
    @Schema(description = "用户姓名", example = "张三")
    private String userName;

    /**
     * 用户年龄
     */
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄不能超过150")
    @Schema(description = "用户年龄", example = "25")
    private Integer userAge;

    /**
     * 用户性别
     */
    @Schema(description = "用户性别", example = "男")
    private String userSex;

    /**
     * 用户电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "用户电话", example = "13800138000")
    private String userTel;

    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    private String imgPath;
}
