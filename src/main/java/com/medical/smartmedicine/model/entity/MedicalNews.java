package com.medical.smartmedicine.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 医疗资讯实体
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
     * 资讯标题
     */
    @NotBlank(message = "资讯标题不能为空")
    private String newsName;

    /**
     * 资讯摘要（列表展示用）
     */
    private String newsSummary;

    /**
     * 封面图OSS路径
     */
    @NotBlank(message = "封面图不能为空")
    private String coverOssPath;

    /**
     * Markdown文件OSS路径
     */
    @NotBlank(message = "Markdown文件不能为空")
    private String markdownOssPath;

    /**
     * 分类
     */
    private String category;

    /**
     * 作者
     */
    private String author;

    /**
     * 状态：0-草稿，1-已发布
     */
    @TableField("status")
    @Builder.Default
    private Integer status = 0;

    /**
     * 浏览量
     */
    @Builder.Default
    private Integer viewCount = 0;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
