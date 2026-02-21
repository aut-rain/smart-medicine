package com.medical.smartmedicine.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮件配置属性
 * 从配置文件中读取邮件相关配置
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    /**
     * 邮件服务器地址
     */
    private String host;

    /**
     * 邮件服务器端口
     */
    private Integer port;

    /**
     * 发件人邮箱账号
     */
    private String username;

    /**
     * 发件人邮箱密码/授权码
     */
    private String password;

    /**
     * 验证码有效期（分钟）
     */
    private Integer valid;

    /**
     * 邮件标题
     */
    private String title;

    /**
     * 邮件模板
     * 占位符：第一个%s为验证码，第二个%s为有效期
     */
    private String template;
}
