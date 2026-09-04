package com.example.maintenance.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.maintenance.global.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth

				// Swagger
				.requestMatchers(
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/v3/api-docs/**",
					"/uploads/report-images/**"
				).permitAll()

				// Auth
				.requestMatchers(
					"/api/auth/signup",
					"/api/auth/login"
				).permitAll()
				
				.requestMatchers("/api/auth/me").authenticated()

				// User 관리: ADMIN만
				.requestMatchers("/api/users/**").hasRole("ADMIN")

				// Technician 관리: MANAGER, ADMIN
				.requestMatchers("/api/technicians/**").hasAnyRole("MANAGER", "ADMIN")

				// Device 관리: MANAGER, ADMIN
				.requestMatchers("/api/devices/**").hasAnyRole("MANAGER", "ADMIN")

				// ErrorType 조회: 로그인 사용자
				.requestMatchers("/api/error-types/**").authenticated()

				// RepairReport 생성: TECHNICIAN
				.requestMatchers(HttpMethod.POST, "/api/repair-reports").hasRole("TECHNICIAN")

				// ReportImage 업로드/삭제: TECHNICIAN
				.requestMatchers(HttpMethod.POST, "/api/repair-reports/*/images").hasRole("TECHNICIAN")
				.requestMatchers(HttpMethod.DELETE, "/api/repair-reports/*/images/*").hasRole("TECHNICIAN")

				// ReportImage 조회: 로그인 사용자
				.requestMatchers(HttpMethod.GET, "/api/repair-reports/*/images").authenticated()

				// RepairReport 상태 변경: TECHNICIAN
				.requestMatchers(HttpMethod.PATCH, "/api/repair-reports/*/submit").hasRole("TECHNICIAN")
				.requestMatchers(HttpMethod.PATCH, "/api/repair-reports/*/resubmit").hasRole("TECHNICIAN")

				// RepairReport 상태 변경: MANAGER, ADMIN
				.requestMatchers(HttpMethod.PATCH, "/api/repair-reports/*/review").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers(HttpMethod.PATCH, "/api/repair-reports/*/approve").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers(HttpMethod.PATCH, "/api/repair-reports/*/reject").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/repair-reports/*/export").hasAnyRole("MANAGER", "ADMIN")

				// RepairReport 수정/삭제
				.requestMatchers(HttpMethod.PATCH, "/api/repair-reports/*").hasRole("TECHNICIAN")
				.requestMatchers(HttpMethod.DELETE, "/api/repair-reports/*").hasRole("TECHNICIAN")

				// RepairReport 조회
				.requestMatchers(HttpMethod.GET, "/api/repair-reports/**").authenticated()

				// 그 외 요청은 인증 필요
				.anyRequest().authenticated()
			)
			.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}