package com.example.maintenance.domain.user.dto;

import com.example.maintenance.domain.user.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 50자 이하여야 합니다.")
	String name,

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	@Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
	String email,

	@NotBlank(message = "비밀번호는 필수입니다.")
	String password,

	@NotNull(message = "역할은 필수입니다.")
	UserRole role
) {
}