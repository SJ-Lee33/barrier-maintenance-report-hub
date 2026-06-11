package com.example.maintenance.domain.device.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record DeviceUpdateRequest(

	@Size(max = 255, message = "장비 위치는 255자 이하여야 합니다.")
	String location,

	@Size(max = 100, message = "장비 모델명은 100자 이하여야 합니다.")
	String modelName,

	LocalDateTime installedAt
) {
}