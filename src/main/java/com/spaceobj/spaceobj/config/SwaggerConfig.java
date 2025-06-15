package com.spaceobj.spaceobj.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SpaceObj API")
                        .version("1.0.0")
                        .description("SpaceObj 学生管理系统 API 文档")
                        .contact(new Contact()
                                .name("SpaceObj Team")
                                .email("contact@spaceobj.com")
                                .url("https://spaceobj.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
} 