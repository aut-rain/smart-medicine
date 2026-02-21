package com.medical.smartmedicine.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 疾病实体
 *
 * @author ZZY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("illness")
public class Illness implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 疾病所属种类id
     */
    @NotNull(message = "疾病分类不能为空")
    private Integer kindId;

    /**
     * 疾病的名字
     */
    @NotBlank(message = "疾病名称不能为空")
    private String illnessName;

    /**
     * 引起的原因
     */
    private String includeReason;

    /**
     * 主要的症状
     */
    private String illnessSymptom;

    /**
     * 特殊的症状
     */
    private String specialSymptom;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 关联的疾病分类(非数据库字段)
     */
    @TableField(exist = false)
    private IllnessKind kind;

    /**
     * 关联的药品(非数据库字段)
     */
    @TableField(exist = false)
    private IllnessMedicine illnessMedicine;
}
