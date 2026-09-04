package com.example.maintenance.global.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public record FileStorageProperties(
	String uploadDir,
	long maxImageSize,
	String exportDir
) {
}