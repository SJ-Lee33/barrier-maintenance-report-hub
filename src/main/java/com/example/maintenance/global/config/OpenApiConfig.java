package com.example.maintenance.global.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(
		title = "Barrier Maintenance Report Hub API",
		description = "주차차단기 현장 유지보수 리포트 관리 백엔드 API",
		version = "v1.0.0"
	)
)
@Configuration
public class OpenApiConfig {
}