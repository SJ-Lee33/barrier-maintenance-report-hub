package com.example.maintenance.global.config;

// TODO: 추후에 인증이 생기면, 권한을 TECHNICIAN / MANAGER / ADMIN 으로 나누어 부여
// 일단은 다 열어둔다

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll() // 개발 편의를 위해 모든 api 요청 허용
			);

		return http.build();
	}
}