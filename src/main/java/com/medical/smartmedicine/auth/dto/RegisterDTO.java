package com.medical.smartmedicine.auth.dto;

import com.medical.smartmedicine.common.constant.BusinessConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户注册请求DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户注册请求")
public class RegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = BusinessConstant.ACCOUNT_MIN_LENGTH, 
          max = BusinessConstant.ACCOUNT_MAX_LENGTH, 
          message = "账号长度为4-20位")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "账号只能包含字母和数字")
    @Schema(description = "用户账号", example = "zhangsan")
    private String userAccount;

    /**
     * 用户密码
     */
    @NotBlank(message = "用户密码不能为空")
    @Size(min = BusinessConstant.PASSWORD_MIN_LENGTH, 
          max = BusinessConstant.PASSWORD_MAX_LENGTH, 
          message = "密码长度为6-20位")
    @Schema(description = "用户密码", example = "123456")
    private String userPwd;

    /**
     * 用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    @Schema(description = "用户姓名", example = "张三")
    private String userName;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "用户邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "用户邮箱", example = "zhangsan@example.com")
    private String userEmail;

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
     * 邮箱验证码
     */
    @NotBlank(message = "邮箱验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度为6位")
    @Schema(description = "邮箱验证码", example = "123456")
    private String emailCode;
}
