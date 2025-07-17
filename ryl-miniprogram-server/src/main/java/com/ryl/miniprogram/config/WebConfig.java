package com.ryl.miniprogram.config;

import com.ryl.miniprogram.security.JwtAuthInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.util.List;

/**
 * Web配置类
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Bean
    @Qualifier("uploadPath")
    public String uploadPath() {
        // 从模块目录回退一级到项目根目录，然后进入uploads文件夹
        String path = new File("../uploads").getAbsolutePath();
        log.info("全局上传文件绝对路径配置为: {}", path);
        // 为了可移植性，我们返回相对路径，让各个组件自己处理
        return "../uploads";
    }
    
    /**
     * 添加跨域支持
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
     * 添加JWT认证拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/account/login",
                        "/account/register",
                        "/account/check-contact",
                        "/wechat/login",
                        "/error",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/uploads/**"  // 允许访问上传的文件
                );
    }
    
    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath() + "/");
        
        // 添加Swagger UI资源映射
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }
    
    /**
     * 配置消息转换器，确保请求正确解析
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }
    
    /**
     * 配置文件上传解析器
     * 使用Spring Boot内置的StandardServletMultipartResolver
     */
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    /**
     * 配置Multipart相关参数
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置上传文件总大小不超过20MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(20));
        // 设置单个文件大小不超过10MB
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        
        // 创建临时目录
        File tempDir = new File(uploadPath(), "temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        // setLocation需要一个绝对路径，但这是框架内部要求，不影响我们应用代码的可移植性
        factory.setLocation(tempDir.getAbsolutePath());
        
        return factory.createMultipartConfig();
    }
} 