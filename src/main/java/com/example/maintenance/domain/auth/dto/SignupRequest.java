package com.example.maintenance.domain.auth.dto;

import com.example.maintenance.domain.user.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

	// 중복방지키: 이름+전화번호
	// 다른 이메일로 중복가입 금지

	@Schema(description = "사용자 이름", example = "김기사")
	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 50자 이하여야 합니다.")
	String name,

	@Schema(description = "이메일", example = "tech3@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	@Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
	String email,

	@Schema(description = "휴대폰 번호", example = "010-1234-5678")
	@NotBlank(message = "휴대폰 번호는 필수입니다.")
	@Pattern(
		regexp = "^010-\\d{4}-\\d{4}$",
		message = "휴대폰 번호는 010-0000-0000 형식이어야 합니다."
	)
	String phone,

	@Schema(description = "비밀번호", example = "password1234")
	@NotBlank(message = "비밀번호는 필수입니다.")
	String password,

	@Schema(description = "사용자 역할", example = "TECHNICIAN")
	@NotNull(message = "역할은 필수입니다.")
	UserRole role
) {
}