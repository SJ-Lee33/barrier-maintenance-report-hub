package com.example.maintenance.domain.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.user.dto.UserCreateRequest;
import com.example.maintenance.domain.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User API", description = "개발용 사용자 등록 및 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	// 개발용 임시 create api
	@PostMapping
	public ResponseEntity<UserResponse> createUser(
		@Valid @RequestBody UserCreateRequest request
	) {
		UserResponse response = userService.createUser(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getUsers() {
		List<UserResponse> responses = userService.getUsers();

		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUser(
		@PathVariable Long userId
	) {
		UserResponse response = userService.getUser(userId);

		return ResponseEntity.ok(response);
	}
}