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
 * 科普视频实体
 *
 * @author ZZY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("science_video")
public class ScienceVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 视频标题
     */
    @NotBlank(message = "视频标题不能为空")
    private String title;

    /**
     * 视频描述
     */
    private String description;

    /**
     * 视频展示图片
     */
    @TableField("imgPath")
    private String imgPath;

    /**
     * 视频链接
     */
    @NotBlank(message = "视频链接不能为空")
    private String link;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
