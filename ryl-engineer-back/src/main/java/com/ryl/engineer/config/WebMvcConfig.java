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

/**
 * Web配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private ObjectMapper objectMapper; // 注入JacksonConfig配置的ObjectMapper
    
    @Autowired
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;
    
    // 是否使用绝对路径（从配置中读取，默认为true）
    @Value("${app.upload.use-absolute-path:true}")
    private boolean useAbsolutePath;
    
    // 上传文件的基础路径（从配置中读取，默认为当前工作目录下的uploads目录）
    @Value("${app.upload.base-path:}")
    private String configuredUploadPath;

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
                .addPathPatterns("/api/v1/**")
                // 排除登录、注册等不需要认证的路径
                .excludePathPatterns(
                    "/api/v1/user/login",
                    "/api/v1/user/register",
                    "/api/v1/user/forgot-password/**",
                    "/api/v1/user/reset-password",
                    "/api/v1/uploads/**"
                );
        
        System.out.println("JWT认证拦截器注册完成");
    }
    
    /**
     * 静态资源处理器配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取上传文件的基础路径
        String baseUploadPath;
        if (useAbsolutePath) {
            // 如果配置了自定义路径，则使用配置的路径
            if (configuredUploadPath != null && !configuredUploadPath.isEmpty()) {
                baseUploadPath = configuredUploadPath;
            } else {
                // 否则使用默认的D:/uploads路径
                baseUploadPath = "D:/uploads";
            }
        } else {
            // 使用相对于Web应用的路径
            baseUploadPath = servletContext.getRealPath("/uploads");
        }
        
        // 确保路径以分隔符结尾
        if (!baseUploadPath.endsWith(File.separator)) {
            baseUploadPath += File.separator;
        }
        
        // 配置上传文件访问路径
        registry.addResourceHandler("/api/v1/uploads/**")
                .addResourceLocations("file:" + baseUploadPath);
                
        // 添加其他静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
                
        // 为测试添加日志输出
        System.out.println("静态资源路径配置完成，上传路径为: " + baseUploadPath);
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