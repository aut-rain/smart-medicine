package com.medical.smartmedicine.user.dto;

import com.medical.smartmedicine.common.constant.BusinessConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 修改密码请求DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "修改密码请求")
public class PasswordUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", example = "123456")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = BusinessConstant.PASSWORD_MIN_LENGTH, 
          max = BusinessConstant.PASSWORD_MAX_LENGTH, 
          message = "密码长度为6-20位")
    @Schema(description = "新密码", example = "654321")
    private String newPassword;
}
