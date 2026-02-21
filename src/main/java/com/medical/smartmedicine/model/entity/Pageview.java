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

/**
 * 浏览量统计实体
 *
 * @author ZZY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("pageview")
public class Pageview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 浏览量主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 浏览量
     */
    @NotNull(message = "浏览量不能为空")
    private Integer pageviews;

    /**
     * 病的id
     */
    @NotNull(message = "疾病ID不能为空")
    private Integer illnessId;
}
