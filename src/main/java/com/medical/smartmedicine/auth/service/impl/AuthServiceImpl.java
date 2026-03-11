package com.medical.smartmedicine.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.smartmedicine.auth.dto.LoginDTO;
import com.medical.smartmedicine.auth.dto.RegisterDTO;
import com.medical.smartmedicine.auth.service.AuthService;
import com.medical.smartmedicine.auth.vo.TokenVO;
import com.medical.smartmedicine.common.client.EmailClient;
import com.medical.smartmedicine.common.constant.RedisConstant;
import com.medical.smartmedicine.common.constant.SecurityConstant;
import com.medical.smartmedicine.common.enums.ResultCode;
import com.medical.smartmedicine.common.enums.RoleEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.config.security.JwtTokenProvider;
import com.medical.smartmedicine.mapper.UserMapper;
import com.medical.smartmedicine.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final EmailClient emailClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenVO login(LoginDTO loginDTO) {
        log.info("用户登录: userAccount={}", loginDTO.getUserAccount());

        // 1. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, loginDTO.getUserAccount());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(loginDTO.getUserPwd(), user.getUserPwd())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 生成Token
        TokenVO tokenVO = generateToken(user);

        log.info("用户登录成功: userId={}, userAccount={}", user.getId(), user.getUserAccount());
        return tokenVO;
    }

    @Override
    public TokenVO register(RegisterDTO registerDTO) {
        log.info("用户注册: userAccount={}, userEmail={}", 
                registerDTO.getUserAccount(), registerDTO.getUserEmail());

        // 1. 验证邮箱验证码
        if (StrUtil.isNotBlank(registerDTO.getEmailCode())) {
            if (!verifyEmailCode(registerDTO.getUserEmail(), registerDTO.getEmailCode())) {
                throw new BusinessException(ResultCode.EMAIL_CODE_INVALID);
            }
        }

        // 2. 检查账号是否已存在
        LambdaQueryWrapper<User> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper.eq(User::getUserAccount, registerDTO.getUserAccount());
        if (userMapper.selectCount(accountWrapper) > 0) {
            throw new BusinessException(ResultCode.ACCOUNT_EXISTS);
        }

        // 3. 检查邮箱是否已注册
        if (StrUtil.isNotBlank(registerDTO.getUserEmail())) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getUserEmail, registerDTO.getUserEmail());
            if (userMapper.selectCount(emailWrapper) > 0) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
        }

        // 4. 创建用户
        User user = User.builder()
                .userAccount(registerDTO.getUserAccount())
                .userName(registerDTO.getUserName())
                .userPwd(passwordEncoder.encode(registerDTO.getUserPwd()))
                .userAge(registerDTO.getUserAge())
                .userSex(registerDTO.getUserSex())
                .userEmail(registerDTO.getUserEmail())
                .userTel(registerDTO.getUserTel())
                .roleStatus(RoleEnum.USER.getStatus())
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        userMapper.insert(user);

        // 5. 生成Token
        TokenVO tokenVO = generateToken(user);

        log.info("用户注册成功: userId={}, userAccount={}", user.getId(), user.getUserAccount());
        return tokenVO;
    }

    @Override
    public void logout(String token) {
        // 提取纯Token(去除Bearer前缀)
        if (token.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            token = token.substring(SecurityConstant.TOKEN_PREFIX.length());
        }

        // 获取Token剩余有效时间并加入黑名单
        try {
            long ttl = jwtTokenProvider.getRemainingValidity(token);
            if (ttl > 0) {
                String key = RedisConstant.TOKEN_BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "1", ttl, TimeUnit.MILLISECONDS);
                log.info("Token已加入黑名单");
            }
        } catch (Exception e) {
            log.warn("Token解析失败,可能已过期: {}", e.getMessage());
        }
    }

    @Override
    public TokenVO refreshToken(String refreshToken) {
        try {
            // 验证refreshToken
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                throw new BusinessException(ResultCode.TOKEN_REFRESH_FAILED);
            }

            // 从Token中获取用户ID
            Integer userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

            // 查询用户
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND);
            }

            // 生成新Token
            return generateToken(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            throw new BusinessException(ResultCode.TOKEN_REFRESH_FAILED);
        }
    }

    @Override
    public void sendEmailCode(String email) {
        // 生成6位验证码
        String code = RandomUtil.randomNumbers(6);

        // 存储到Redis,5分钟有效
        String key = RedisConstant.EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, 
                RedisConstant.EMAIL_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 调用邮件服务异步发送验证码
        emailClient.sendEmailCodeAsync(email, code);

        log.info("验证码已生成并发送到邮箱: {}, code={}", email, code);
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        String key = RedisConstant.EMAIL_CODE_PREFIX + email;
        String cachedCode = redisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(cachedCode)) {
            return false;
        }

        boolean verified = cachedCode.equals(code);
        if (verified) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
        }

        return verified;
    }

    /**
     * 生成Token
     */
    private TokenVO generateToken(User user) {
        Date now = new Date();

        // 使用JwtTokenProvider生成Token
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), 
                user.getUserAccount(), 
                user.getRoleStatus()
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), 
                user.getUserAccount()
        );

        return TokenVO.builder()
                .userId(user.getId())
                .userAccount(user.getUserAccount())
                .userName(user.getUserName())
                .roleStatus(user.getRoleStatus())
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200L) // 2小时
                .issuedAt(now)
                .build();
    }
}
