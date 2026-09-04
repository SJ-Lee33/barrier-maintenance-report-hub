package com.example.maintenance.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

public abstract class ApiTestSupport {

	protected static final String TECH_EMAIL = "tech01@example.com";
	protected static final String MANAGER_EMAIL = "manager01@example.com";
	protected static final String PASSWORD = "password1234";

	@Autowired
	protected MockMvc mockMvc;

	@BeforeEach
	void setUpTestData() throws Exception {
		signupIfNeeded(
			"기사01",
			TECH_EMAIL,
			"010-1000-0001",
			"TECHNICIAN"
		);

		signupIfNeeded(
			"관리자01",
			MANAGER_EMAIL,
			"010-2000-0001",
			"MANAGER"
		);

		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer techUserId = getCurrentUserId(techToken);

		createTechnicianProfileIfNeeded(managerToken, techUserId);
		createDeviceIfNeeded(managerToken);
	}

	protected String login(String email) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "%s",
					  "password": "%s"
					}
					""".formatted(email, PASSWORD)))
			.andReturn();

		String response = result.getResponse().getContentAsString();

		if (result.getResponse().getStatus() != 200) {
			throw new IllegalStateException("로그인 실패: " + response);
		}

		return JsonPath.read(response, "$.accessToken");
	}

	protected String bearer(String token) {
		return "Bearer " + token;
	}

	private void signupIfNeeded(
		String name,
		String email,
		String phone,
		String role
	) throws Exception {
		mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "%s",
					  "email": "%s",
					  "phone": "%s",
					  "password": "%s",
					  "role": "%s"
					}
					""".formatted(name, email, phone, PASSWORD, role)))
			.andReturn();
	}

	private Integer getCurrentUserId(String token) throws Exception {
		String response = mockMvc.perform(get("/api/auth/me")
				.header("Authorization", bearer(token)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(response, "$.userId");
	}

	private void createTechnicianProfileIfNeeded(
		String managerToken,
		Integer techUserId
	) throws Exception {
		mockMvc.perform(post("/api/technicians")
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "userId": %d,
					  "phone": "010-1000-0001",
					  "department": "유지보수팀",
					  "empNo": "T-001"
					}
					""".formatted(techUserId)))
			.andReturn();
	}

	private void createDeviceIfNeeded(String managerToken) throws Exception {
		mockMvc.perform(post("/api/devices")
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "serialNo": "GATE-2024-001",
					  "location": "서울 강남구 A주차장 입구",
					  "modelName": "BARRIER-X100"
					}
					"""))
			.andReturn();
	}
}