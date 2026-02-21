package com.medical.smartmedicine.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 浏览历史实体
 *
 * @author ZZY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("history")
public class History implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 浏览历史关联用户id
     */
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    /**
     * 浏览历史类型
     */
    @NotNull(message = "操作类型不能为空")
    private Integer operateType;

    /**
     * 浏览历史关键字
     */
    private String keyword;

    /**
     * 浏览时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
