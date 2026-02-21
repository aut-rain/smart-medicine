package com.medical.smartmedicine.auth.service;

import com.medical.smartmedicine.auth.dto.LoginDTO;
import com.medical.smartmedicine.auth.dto.RegisterDTO;
import com.medical.smartmedicine.auth.vo.TokenVO;

/**
 * 认证服务接口
 * 负责用户注册、登录、登出等认证相关业务
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return Token信息
     */
    TokenVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return Token信息
     */
    TokenVO register(RegisterDTO registerDTO);

    /**
     * 用户登出
     * 将Token加入黑名单,使其失效
     *
     * @param token 访问令牌
     */
    void logout(String token);

    /**
     * 刷新Token
     * 使用refreshToken换取新的accessToken
     *
     * @param refreshToken 刷新令牌
     * @return 新的Token信息
     */
    TokenVO refreshToken(String refreshToken);

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     */
    void sendEmailCode(String email);

    /**
     * 验证邮箱验证码
     *
     * @param email 邮箱地址
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyEmailCode(String email, String code);
}
