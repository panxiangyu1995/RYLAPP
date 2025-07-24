package com.ryl.engineer.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件URL转换器
 * 负责将数据库中存储的相对路径转换为前端可访问的完整URL
 */
@Component
public class FileUrlConverter {

    @Value("${ryl.file.url-prefix}")
    private String urlPrefix;

    /**
     * 将单个相对路径转换为完整URL
     *
     * @param relativePath 数据库中存储的相对路径
     * @return 完整的URL，如果输入为空或已经是完整URL，则返回原值
     */
    public String toFullUrl(String relativePath) {
        // 1. 健壮性检查：如果路径为空，直接返回
        if (!StringUtils.hasText(relativePath)) {
            return relativePath;
        }

        // 2. 幂等性设计：如果已经是完整URL，直接返回
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }

        // 3. 执行拼接
        return urlPrefix + relativePath;
    }

    /**
     * 将相对路径列表转换为完整URL列表
     *
     * @param relativePaths 相对路径列表
     * @return 完整URL列表
     */
    public List<String> toFullUrls(List<String> relativePaths) {
        if (relativePaths == null || relativePaths.isEmpty()) {
            return Collections.emptyList();
        }
        return relativePaths.stream()
                .map(this::toFullUrl)
                .collect(Collectors.toList());
    }

    /**
     * 优雅地处理DTO中URL字段的转换
     *
     * @param dto       需要处理的DTO对象
     * @param urlGetter 获取URL字段的方法引用 (e.g., User::getAvatar)
     * @param urlSetter 设置URL字段的方法引用 (e.g., User::setAvatar)
     * @param <T>       DTO的类型
     */
    public <T> void processDto(T dto, Function<T, String> urlGetter, BiConsumer<T, String> urlSetter) {
        if (dto == null) {
            return;
        }
        String relativePath = urlGetter.apply(dto);
        String fullUrl = toFullUrl(relativePath);
        urlSetter.accept(dto, fullUrl);
    }
} 