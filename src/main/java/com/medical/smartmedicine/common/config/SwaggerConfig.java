package com.medical.smartmedicine.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI与Knife4j配置
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Smart Medicine System API", // 项目名称
                description = "智慧医疗系统后端服务接口文档", // 项目描述
                version = "1.0.0", // API版本号
                contact = @Contact( // 联系人信息
                        name = "zzy",
                        email = "2545946621@qq.com",
                        url = "https://github.com/aut-rain"
                )
//                license = @License( // 许可证信息
//                        name = "Apache 2.0",
//                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
//                )
        ),
        // 可以定义多个服务器地址，方便切换环境
        servers = {
                @Server(url = "http://localhost:8080", description = "本地开发环境")
//                @Server(url = "https://api-dev.smartmedicine.com", description = "测试环境"),
//                @Server(url = "https://api.smartmedicine.com", description = "生产环境")
        }

)

public class SwaggerConfig {
}
