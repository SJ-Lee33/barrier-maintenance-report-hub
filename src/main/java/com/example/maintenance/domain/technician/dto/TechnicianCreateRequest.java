package com.example.maintenance.domain.technician.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TechnicianCreateRequest(

	@NotNull(message = "사용자 ID는 필수입니다.")
	Long userId,

	@NotBlank(message = "전화번호는 필수입니다.")
	@Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
	String phone,

	@NotBlank(message = "부서는 필수입니다.")
	@Size(max = 50, message = "부서는 50자 이하여야 합니다.")
	String department,

	@NotBlank(message = "사번은 필수입니다.")
	@Size(max = 30, message = "사번은 30자 이하여야 합니다.")
	String empNo
) {
}