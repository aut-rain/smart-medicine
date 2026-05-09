package com.medical.smartmedicine.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 医疗资讯正文缓存实体
 * 将OSS中的Markdown正文同步到MySQL，供严格检索使用。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("medical_news_content")
public class MedicalNewsContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer newsId;

    private String plainContent;

    private String contentHash;

    private Date createTime;

    private Date updateTime;
}
