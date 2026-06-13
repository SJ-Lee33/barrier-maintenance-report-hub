package com.example.maintenance.domain.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.user.dto.UserCreateRequest;
import com.example.maintenance.domain.user.dto.UserResponse;
import com.example.maintenance.global.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponse createUser(UserCreateRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new IllegalArgumentException("이미 등록된 이메일입니다.");
		}

		User user = new User(
			request.name(),
			request.email(),
			passwordEncoder.encode(request.password()),
			request.role()
		);

		User savedUser = userRepository.save(user);

		return UserResponse.from(savedUser);
	}

	public List<UserResponse> getUsers() {
		return userRepository.findAll()
			.stream()
			.map(UserResponse::from)
			.toList();
	}

	public UserResponse getUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		return UserResponse.from(user);
	}
}