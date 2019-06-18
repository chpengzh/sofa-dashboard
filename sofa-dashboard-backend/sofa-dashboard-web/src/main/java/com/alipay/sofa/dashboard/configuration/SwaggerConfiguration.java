package com.alipay.sofa.dashboard.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerUI 相关配置
 */
@EnableSwagger2
@Configuration
@ConditionalOnExpression("${swagger.enable:true}")
public class SwaggerConfiguration {

    private static final String BASE_PACKAGE = "com.alipay.sofa.dashboard";

    /**
     * 应用相关元数据
     *
     * @param appName 应用名
     * @return Docket instance by swagger2
     */
    @Bean
    public Docket api(@Value("${spring.application.name}") String appName) {
        ApiInfo apiInfo = new ApiInfoBuilder()
            .title(appName)
            .version("1.0")
            .description("SOFADashboard API Document.")
            .build();

        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo)
            .select()
            .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
            .paths(PathSelectors.any())
            .build();
    }

}
