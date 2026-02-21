package com.medical.smartmedicine.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 疾病药品关联实体
 *
 * @author ZZY
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("illness_medicine")
public class IllnessMedicine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 疾病id
     */
    @NotNull(message = "疾病ID不能为空")
    private Integer illnessId;

    /**
     * 药物id
     */
    @NotNull(message = "药品ID不能为空")
    private Integer medicineId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
