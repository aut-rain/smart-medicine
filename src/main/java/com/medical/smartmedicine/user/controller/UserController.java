package com.medical.smartmedicine.user.controller;

import com.medical.smartmedicine.common.base.PageQuery;
import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.user.dto.PasswordUpdateDTO;
import com.medical.smartmedicine.user.dto.UserUpdateDTO;
import com.medical.smartmedicine.user.service.UserService;
import com.medical.smartmedicine.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理Controller
 * 处理用户信息的查询、更新等操作
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.USER_PATH)
@Tag(name = "用户管理", description = "用户信息管理相关接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserVO> getCurrentUser() {
        log.info("获取当前用户信息");
        UserVO userVO = userService.getCurrentUser();
        
        return Result.success(userVO);
    }

    /**
     * 更新个人资料
     *
     * @param updateDTO 更新信息
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    @Operation(summary = "更新个人资料", description = "更新当前用户的个人信息")
    public Result<UserVO> updateProfile(@Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("更新个人资料: updateDTO={}", updateDTO);
        UserVO userVO = userService.updateProfile(updateDTO);
        
        return Result.success("更新成功", userVO);
    }

    /**
     * 修改密码
     *
     * @param passwordDTO 密码修改信息
     * @return 操作结果
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO passwordDTO) {
        log.info("修改密码请求");
        userService.updatePassword(passwordDTO);
        
        return Result.success("密码修改成功,请重新登录");
    }

    /**
     * 用户列表(管理员)
     *
     * @param query 分页查询参数
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "用户列表(管理员)", description = "分页查询用户列表,仅管理员可访问")
    public Result<PageResult<UserVO>> listUsers(@Valid PageQuery query) {
        log.info("查询用户列表: page={}, size={}, keyword={}", 
                query.getPage(), query.getSize(), query.getKeyword());
        PageResult<UserVO> result = userService.listUsers(query);
        
        return Result.success(result);
    }

    /**
     * 获取指定用户信息(管理员)
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取指定用户信息(管理员)", description = "根据用户ID获取用户详细信息")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Integer id) {
        log.info("获取用户信息: id={}", id);
        UserVO userVO = userService.getUserById(id);
        
        return Result.success(userVO);
    }

    /**
     * 删除用户(管理员)
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户(管理员)", description = "删除指定用户,仅管理员可操作")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Integer id) {
        log.info("删除用户: id={}", id);
        userService.deleteUser(id);
        
        return Result.success("删除成功");
    }
}
