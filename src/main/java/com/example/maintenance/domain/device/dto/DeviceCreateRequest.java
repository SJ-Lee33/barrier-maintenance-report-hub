package com.example.maintenance.domain.device.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeviceCreateRequest(

	@NotBlank(message = "장비 시리얼 번호는 필수입니다.")
	@Size(max = 50, message = "장비 시리얼 번호는 50자 이하여야 합니다.")
	String serialNo,

	@NotBlank(message = "장비 위치는 필수입니다.")
	@Size(max = 255, message = "장비 위치는 255자 이하여야 합니다.")
	String location,

	@NotBlank(message = "장비 모델명은 필수입니다.")
	@Size(max = 100, message = "장비 모델명은 100자 이하여야 합니다.")
	String modelName,

	LocalDateTime installedAt
) {
}