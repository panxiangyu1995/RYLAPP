package com.ryl.engineer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.ryl.engineer.interceptor.JwtAuthenticationInterceptor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Web配置类
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${ryl.file.upload-path}")
    private String uploadPath;

    @Autowired
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Autowired
    private ObjectMapper objectMapper; // 注入JacksonConfig配置的ObjectMapper

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    /**
     * 添加拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT认证拦截器，拦截所有API请求
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                // 排除登录、注册等不需要认证的路径
                .excludePathPatterns(
                    "/api/v1/user/login",
                    "/api/v1/user/register",
                    "/api/v1/uploads/**"
                );
        
        System.out.println("JWT认证拦截器注册完成");
    }
    
    /**
     * 静态资源处理器配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保物理路径以文件分隔符结尾
        String effectiveUploadPath = uploadPath.endsWith(File.separator) ? uploadPath : uploadPath + File.separator;

        // 将 /api/v1/uploads/** 路径映射到配置的物理磁盘位置
        registry.addResourceHandler("/api/v1/uploads/**")
                .addResourceLocations("file:" + effectiveUploadPath);

        log.info("配置静态资源映射: /api/v1/uploads/** -> {}", "file:" + effectiveUploadPath);
    }

    /**
     * 消息转换器配置
     * 使用JacksonConfig配置的ObjectMapper
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper); // 使用JacksonConfig中配置的ObjectMapper
        converters.add(0, converter);
    }
} 