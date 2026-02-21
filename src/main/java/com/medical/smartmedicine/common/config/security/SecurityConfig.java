package com.medical.smartmedicine.common.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * 配置JWT认证、权限控制和会话管理
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("配置Spring Security过滤器链");

        http
                // 禁用CSRF(使用JWT不需要CSRF保护)
                .csrf(AbstractHttpConfigurer::disable)

                // 启用CORS
                .cors(cors -> cors.configure(http))

                // 配置会话管理为无状态
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 未认证处理
                        .accessDeniedHandler(jwtAccessDeniedHandler) // 权限不足处理
                )

                // 配置URL访问权限
                .authorizeHttpRequests(auth -> auth
                        // 公开接口 - 认证相关(登录、注册、刷新Token)
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh-token",
                                "/api/v1/auth/email-code"
                        ).permitAll()

                        // 公开接口 - Swagger文档
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/doc.html",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 公开接口 - 健康检查和错误页面
                        .requestMatchers(
                                "/actuator/**",
                                "/error"
                        ).permitAll()

                        // 公开接口 - 静态资源
                        .requestMatchers(
                                "/static/**",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // 公开接口 - 疾病和药品查询(根据业务需求，允许游客访问)
                        .requestMatchers(
                                "/api/v1/illnesses/**",
                                "/api/v1/medicines/**"
                        ).permitAll()

                        // 公开接口 - 医疗资讯查询(允许游客访问)
                        .requestMatchers(
                                "/api/v1/medical-news/**"
                        ).permitAll()

                        // 公开接口 - AI聊天(根据业务需求，可以考虑开放或需要认证)
                        .requestMatchers(
                                "/api/v1/ai-chat/**"
                        ).permitAll()

                        // 管理员专用接口
                        .requestMatchers(
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")

                        // 其他所有接口都需要认证
                        .anyRequest().authenticated()
                )

                // 添加JWT过滤器(在UsernamePasswordAuthenticationFilter之前)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("Spring Security配置完成");
        return http.build();
    }

    /**
     * 密码编码器Bean
     * 使用BCrypt加密算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
