package com.medical.smartmedicine.auth.controller;

import com.medical.smartmedicine.auth.dto.LoginDTO;
import com.medical.smartmedicine.auth.dto.RegisterDTO;
import com.medical.smartmedicine.auth.service.AuthService;
import com.medical.smartmedicine.auth.vo.TokenVO;
import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证管理Controller
 * 处理用户注册、登录、登出等认证相关操作
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.AUTH_PATH)
@Tag(name = "认证管理", description = "用户注册、登录、登出相关接口")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return Token信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过账号密码登录系统")
    public Result<TokenVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: userAccount={}", loginDTO.getUserAccount());
        TokenVO tokenVO = authService.login(loginDTO);
        
        return Result.success("登录成功", tokenVO);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return Token信息
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册账号")
    public Result<TokenVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求: userAccount={}, userEmail={}", 
                registerDTO.getUserAccount(), registerDTO.getUserEmail());
        TokenVO tokenVO = authService.register(registerDTO);
        
        return Result.success("注册成功", tokenVO);
    }

    /**
     * 用户登出
     *
     * @param token 访问令牌
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录,清除Token")
    public Result<Void> logout(
            @Parameter(description = "访问令牌", required = true)
            @RequestHeader("Authorization") String token) {
        log.info("用户登出请求: token={}", token);
        authService.logout(token);
        
        return Result.success("登出成功");
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的Token信息
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "使用refreshToken刷新accessToken")
    public Result<TokenVO> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @RequestParam String refreshToken) {
        log.info("刷新Token请求");
        TokenVO tokenVO = authService.refreshToken(refreshToken);
        
        return Result.success("刷新成功", tokenVO);
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @return 操作结果
     */
    @PostMapping("/email-code")
    @Operation(summary = "发送邮箱验证码", description = "发送注册验证码到指定邮箱")
    public Result<Void> sendEmailCode(
            @Parameter(description = "邮箱地址", required = true, example = "user@example.com")
            @RequestParam String email) {
        log.info("发送邮箱验证码请求: email={}", email);
        authService.sendEmailCode(email);
        
        return Result.success("验证码已发送,请查收邮件");
    }

    /**
     * 发送邮箱验证码（兼容接口）
     *
     * @param email 邮箱地址
     * @return 操作结果
     */
    @PostMapping("/send-code")
    @Operation(summary = "发送邮箱验证码", description = "发送注册验证码到指定邮箱（兼容旧版API）")
    public Result<Void> sendCode(
            @Parameter(description = "邮箱地址", required = true, example = "user@example.com")
            @RequestParam String email) {
        return sendEmailCode(email);
    }
}
