package com.example.maintenance.domain.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.auth.dto.LoginRequest;
import com.example.maintenance.domain.auth.dto.LoginResponse;
import com.example.maintenance.domain.auth.dto.SignupRequest;
import com.example.maintenance.domain.user.User;
import com.example.maintenance.domain.user.UserRepository;
import com.example.maintenance.domain.user.dto.UserResponse;
import com.example.maintenance.global.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponse signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new IllegalArgumentException("이미 등록된 이메일입니다.");
		}

		if (userRepository.existsByNameAndPhone(request.name(), request.phone())) {
			throw new IllegalArgumentException("이미 동일한 이름과 휴대폰 번호로 가입된 사용자가 있습니다.");
		}

		User user = new User(
			request.name(),
			request.email(),
			request.phone(),
			passwordEncoder.encode(request.password()),
			request.role()
		);

		User savedUser = userRepository.save(user);

		return UserResponse.from(savedUser);
	}

	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
			.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		return LoginResponse.from(user);
	}
}