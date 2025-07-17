package com.ryl.miniprogram.dto;

import org.springframework.core.io.Resource;

public record FileDownloadResource(Resource resource, String filename) {
} 