package com.ryl.engineer.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson配置类，专门处理日期时间格式
 * 确保前端发送的ISO日期格式能够被正确解析
 */
@Configuration
public class JacksonConfig {

    // 改进的日期时间格式模式，增强对ISO 8601格式的支持
    // 包括处理前端发送的带Z时区标识符的格式
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册JSR310模块（Java 8日期时间API）
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 自定义LocalDateTime反序列化器，处理带Z时区的ISO日期时间格式
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER) {
            @Override
            public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                String dateTimeString = parser.getText();
                
                // 处理带有Z时区标识符的ISO格式日期
                if (dateTimeString != null && dateTimeString.endsWith("Z")) {
                    // 移除Z时区标识符
                    dateTimeString = dateTimeString.substring(0, dateTimeString.length() - 1);
                    // 使用LocalDateTime直接解析不带时区的日期时间
                    return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                
                // 其他格式使用父类默认处理
                return super.deserialize(parser, context);
            }
        });
        
        // 添加序列化器，确保输出格式一致
        javaTimeModule.addSerializer(
            LocalDateTime.class, 
            new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        
        // 注册模块
        objectMapper.registerModule(javaTimeModule);
        
        // 禁用将日期写为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 宽松处理（允许未知属性）
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return objectMapper;
    }
} 