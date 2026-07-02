package com.example.maintenance.domain.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.auth.dto.LoginRequest;
import com.example.maintenance.domain.auth.dto.LoginResponse;
import com.example.maintenance.domain.auth.dto.MeResponse;
import com.example.maintenance.domain.auth.dto.SignupRequest;
import com.example.maintenance.domain.user.dto.UserResponse;
import com.example.maintenance.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "회원가입 및 로그인 API")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "회원가입", description = "새 사용자를 등록합니다. 비밀번호는 BCrypt로 암호화되어 저장됩니다.")
	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signup(
		@Valid @RequestBody SignupRequest request
	) {
		UserResponse response = authService.signup(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(response);
	}

	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다. JWT 발급 전 단계에서는 사용자 정보를 반환합니다.")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
		@Valid @RequestBody LoginRequest request
	) {
		LoginResponse response = authService.login(request);

		return ResponseEntity.ok(response);
	}

	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "내 정보 조회", description = "JWT 인증된 현재 로그인 사용자의 정보를 조회합니다.")
	@GetMapping("/me")
	public ResponseEntity<MeResponse> getMyInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		MeResponse response = MeResponse.from(userDetails.getUser());

		return ResponseEntity.ok(response);
	}
}