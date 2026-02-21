package com.medical.smartmedicine.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smartmedicine.common.base.PageQuery;
import com.medical.smartmedicine.common.client.OssClient;
import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.util.UserContextHolder;
import com.medical.smartmedicine.mapper.UserMapper;
import com.medical.smartmedicine.model.entity.User;
import com.medical.smartmedicine.user.dto.PasswordUpdateDTO;
import com.medical.smartmedicine.user.dto.UserUpdateDTO;
import com.medical.smartmedicine.user.service.UserService;
import com.medical.smartmedicine.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 用户服务实现类
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OssClient ossClient;

    @Override
    public UserVO getCurrentUser() {
        // 从ThreadLocal获取当前用户ID
        Integer userId = UserContextHolder.getUserId();
        return getUserById(userId);
    }

    @Override
    public UserVO getUserById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateProfile(UserUpdateDTO updateDTO) {
        // 从ThreadLocal获取当前用户ID
        Integer userId = UserContextHolder.getUserId();

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }

        // 更新用户信息
        if (StrUtil.isNotBlank(updateDTO.getUserName())) {
            user.setUserName(updateDTO.getUserName());
        }
        if (updateDTO.getUserAge() != null) {
            user.setUserAge(updateDTO.getUserAge());
        }
        if (StrUtil.isNotBlank(updateDTO.getUserSex())) {
            user.setUserSex(updateDTO.getUserSex());
        }
        if (StrUtil.isNotBlank(updateDTO.getUserTel())) {
            // 检查手机号是否已被其他用户使用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserTel, updateDTO.getUserTel())
                   .ne(User::getId, userId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ErrorCodeEnum.PHONE_EXISTS);
            }
            user.setUserTel(updateDTO.getUserTel());
        }
        if (StrUtil.isNotBlank(updateDTO.getImgPath())) {
            user.setImgPath(updateDTO.getImgPath());
        }

        user.setUpdateTime(new Date());
        userMapper.updateById(user);

        log.info("用户信息更新成功: userId={}", userId);
        return convertToVO(user);
    }

    @Override
    public void updatePassword(PasswordUpdateDTO passwordDTO) {
        // 从ThreadLocal获取当前用户ID
        Integer userId = UserContextHolder.getUserId();

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getUserPwd())) {
            throw new BusinessException(ErrorCodeEnum.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        user.setUserPwd(passwordEncoder.encode(passwordDTO.getNewPassword()));
        user.setUpdateTime(new Date());
        userMapper.updateById(user);

        log.info("用户密码修改成功: userId={}", userId);
    }

    @Override
    public PageResult<UserVO> listUsers(PageQuery query) {
        // 构建分页对象
        Page<User> page = new Page<>(query.getPage(), query.getSize());

        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(User::getUserAccount, query.getKeyword())
                             .or()
                             .like(User::getUserName, query.getKeyword())
                             .or()
                             .like(User::getUserEmail, query.getKeyword()));
        }
        wrapper.orderByDesc(User::getCreateTime);

        // 执行查询
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为VO
        return PageResult.of(userPage, this::convertToVO);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }

        // 不允许删除管理员
        if (user.getRoleStatus() == 1) {
            throw new BusinessException(ErrorCodeEnum.ADMIN_DELETE_FORBIDDEN);
        }

        userMapper.deleteById(id);
        log.info("用户删除成功: userId={}", id);
    }

    /**
     * 转换User实体为UserVO
     * 排除敏感信息(密码)
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
