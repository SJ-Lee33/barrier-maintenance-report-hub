package com.example.maintenance.domain.auth.dto;

import com.example.maintenance.domain.user.User;
import com.example.maintenance.domain.user.UserRole;

public record LoginResponse(
	Long userId,
	String name,
	String email,
	String phone,
	UserRole role
) {

	public static LoginResponse from(User user) {
		return new LoginResponse(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getPhone(),
			user.getRole()
		);
	}
}