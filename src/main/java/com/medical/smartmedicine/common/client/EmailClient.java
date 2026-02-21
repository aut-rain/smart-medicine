package com.medical.smartmedicine.common.client;

import cn.hutool.core.util.RandomUtil;
import com.medical.smartmedicine.common.config.MailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 邮件服务客户端
 * 提供同步和异步发送邮件功能
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailClient {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 同步发送验证码邮件
     *
     * @param targetEmail 目标邮箱
     * @return 验证码
     */
    public String sendEmailCode(String targetEmail) {
        String verifyCode = RandomUtil.randomNumbers(6);
        String content = String.format(mailProperties.getTemplate(), 
                verifyCode, mailProperties.getValid());
        sendEmail(targetEmail, mailProperties.getTitle(), content);
        log.info("同步发送验证码邮件成功，目标邮箱：{}，验证码：{}", targetEmail, verifyCode);
        return verifyCode;
    }

    /**
     * 异步发送验证码邮件
     *
     * @param targetEmail 目标邮箱
     * @param verifyCode 验证码
     */
    @Async("emailTaskExecutor")
    public void sendEmailCodeAsync(String targetEmail, String verifyCode) {
        try {
            String content = String.format(mailProperties.getTemplate(), 
                    verifyCode, mailProperties.getValid());
            sendEmail(targetEmail, mailProperties.getTitle(), content);
            log.info("异步发送验证码邮件成功，目标邮箱：{}，验证码：{}", targetEmail, verifyCode);
        } catch (Exception e) {
            log.error("异步发送邮件失败，目标邮箱：{}", targetEmail, e);
        }
    }

    /**
     * 发送邮件
     *
     * @param targetEmail 目标邮箱
     * @param subject 邮件主题
     * @param content 邮件内容（支持HTML）
     */
    private void sendEmail(String targetEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(targetEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("邮件发送失败: target={}, subject={}", targetEmail, subject, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}
