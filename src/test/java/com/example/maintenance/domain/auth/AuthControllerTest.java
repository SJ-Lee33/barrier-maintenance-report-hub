package com.example.maintenance.domain.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("회원가입에 성공한다")
	void signup_success() throws Exception {
		String unique = unique();
		String email = "test-tech-" + unique + "@example.com";
		String phone = phone();

		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "테스트기사%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "password1234",
					  "role": "TECHNICIAN"
					}
					""".formatted(unique, email, phone)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.email").value(email))
			.andExpect(jsonPath("$.phone").value(phone))
			.andExpect(jsonPath("$.role").value("TECHNICIAN"));
	}

	@Test
	@DisplayName("중복 이메일이면 회원가입에 실패한다")
	void signup_fail_duplicateEmail() throws Exception {
		String unique = unique();
		String email = "duplicate-email-" + unique + "@example.com";
		String phone1 = phone();
		String phone2 = phone();

		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "중복이메일1%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "password1234",
					  "role": "TECHNICIAN"
					}
					""".formatted(unique, email, phone1)))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "중복이메일2%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "password1234",
					  "role": "TECHNICIAN"
					}
					""".formatted(unique, email, phone2)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("중복 이름과 전화번호이면 회원가입에 실패한다")
	void signup_fail_duplicateNameAndPhone() throws Exception {
		String unique = unique();
		String name = "동명이인테스트" + unique;
		String phone = phone();
		String email1 = "same-name-phone1-" + unique + "@example.com";
		String email2 = "same-name-phone2-" + unique + "@example.com";

		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "password1234",
					  "role": "TECHNICIAN"
					}
					""".formatted(name, email1, phone)))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "password1234",
					  "role": "TECHNICIAN"
					}
					""".formatted(name, email2, phone)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("로그인에 성공한다")
	void login_success() throws Exception {
		String unique = unique();
		String email = "login-test-" + unique + "@example.com";
		String phone = phone();

		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "로그인테스트%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "password1234",
					  "role": "MANAGER"
					}
					""".formatted(unique, email, phone)))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "%s",
					  "password": "password1234"
					}
					""".formatted(email)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists());
	}

	@Test
	@DisplayName("비밀번호가 틀리면 로그인에 실패한다")
	void login_fail_wrongPassword() throws Exception {
		String unique = unique();
		String email = "wrong-password-" + unique + "@example.com";
		String phone = phone();

		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "비밀번호실패테스트%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "password1234",
					  "role": "MANAGER"
					}
					""".formatted(unique, email, phone)))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "%s",
					  "password": "wrong-password"
					}
					""".formatted(email)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("인증 없이 내 정보 조회에 실패한다")
	void me_fail_withoutToken() throws Exception {
		mockMvc.perform(get("/api/auth/me"))
			.andExpect(status().isForbidden());
	}

	private String unique() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	private String phone() {
		String number = UUID.randomUUID().toString()
			.replaceAll("[^0-9]", "");

		if (number.length() < 8) {
			number = number + "12345678";
		}

		return "010-" + number.substring(0, 4) + "-" + number.substring(4, 8);
	}
}