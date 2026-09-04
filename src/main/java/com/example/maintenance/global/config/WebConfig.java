package com.example.maintenance.global.config;

import java.nio.file.Path;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.maintenance.global.storage.FileStorageProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final FileStorageProperties fileStorageProperties;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path uploadPath = Path.of(fileStorageProperties.uploadDir())
			.toAbsolutePath()
			.normalize();

		registry.addResourceHandler("/uploads/report-images/**")
			.addResourceLocations(uploadPath.toUri().toString() + "/");

		Path exportPath = Path.of(fileStorageProperties.exportDir())
			.toAbsolutePath()
			.normalize();

		registry.addResourceHandler("/exports/reports/**")
			.addResourceLocations(exportPath.toUri().toString() + "/");
	}
}