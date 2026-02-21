package com.medical.smartmedicine.user.service;

import com.medical.smartmedicine.common.base.PageQuery;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.user.dto.PasswordUpdateDTO;
import com.medical.smartmedicine.user.dto.UserUpdateDTO;
import com.medical.smartmedicine.user.vo.UserVO;

/**
 * 用户服务接口
 * 负责用户信息管理相关业务
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserVO getCurrentUser();

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Integer id);

    /**
     * 更新个人资料
     *
     * @param updateDTO 更新信息
     * @return 更新后的用户信息
     */
    UserVO updateProfile(UserUpdateDTO updateDTO);

    /**
     * 修改密码
     *
     * @param passwordDTO 密码修改信息
     */
    void updatePassword(PasswordUpdateDTO passwordDTO);

    /**
     * 分页查询用户列表(管理员)
     *
     * @param query 分页查询参数
     * @return 用户列表
     */
    PageResult<UserVO> listUsers(PageQuery query);

    /**
     * 删除用户(管理员)
     *
     * @param id 用户ID
     */
    void deleteUser(Integer id);
}
