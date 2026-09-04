package com.example.maintenance.domain.auth.dto;

import com.example.maintenance.domain.user.User;
import com.example.maintenance.domain.user.UserRole;

public record LoginResponse(
	String accessToken,
	String tokenType,
	Long userId,
	String name,
	String email,
	String phone,
	UserRole role
) {

	public static LoginResponse of(String accessToken, User user) {
		return new LoginResponse(
			accessToken,
			"Bearer",
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getPhone(),
			user.getRole()
		);
	}
}