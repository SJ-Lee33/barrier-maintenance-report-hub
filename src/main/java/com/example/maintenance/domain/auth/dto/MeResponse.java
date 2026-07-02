// 테스트용

package com.example.maintenance.domain.auth.dto;

import com.example.maintenance.domain.user.User;
import com.example.maintenance.domain.user.UserRole;

public record MeResponse(
	Long userId,
	String name,
	String email,
	String phone,
	UserRole role
) {

	public static MeResponse from(User user) {
		return new MeResponse(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getPhone(),
			user.getRole()
		);
	}
}