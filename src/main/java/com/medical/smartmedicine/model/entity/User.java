package com.medical.smartmedicine.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 *
 * @author ZZY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空", groups = {ValidationGroup.Insert.class})
    private String userAccount;

    /**
     * 用户真实名字
     */
    private String userName;

    /**
     * 用户密码
     */
    @NotBlank(message = "用户密码不能为空", groups = {ValidationGroup.Insert.class})
    private String userPwd;

    /**
     * 用户年龄
     */
    private Integer userAge;

    /**
     * 用户性别
     */
    private String userSex;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String userEmail;

    /**
     * 用户电话
     */
    private String userTel;

    /**
     * 角色状态，1代表管理员，0普通用户
     */
    private Integer roleStatus;

    /**
     * 图片的地址
     */
    private String imgPath;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 校验分组 - 插入
     */
    public interface ValidationGroup {
        interface Insert {}
        interface Update {}
    }
}
