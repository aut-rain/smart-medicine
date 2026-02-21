package com.medical.smartmedicine.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 医疗咨询实体
 *
 * @author ZZY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("medical_news")
public class MedicalNews implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 医疗咨询名字
     */
    @NotBlank(message = "咨询标题不能为空")
    private String newsName;

    /**
     * 医疗咨询关键字
     */
    private String newsKey;

    /**
     * 咨询的详细内容
     */
    @NotBlank(message = "咨询内容不能为空")
    private String newsContent;

    /**
     * 包含的图片地址
     */
    private String imgPath;

    /**
     * 关联的疾病id
     */
    private Integer relationIllness;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
