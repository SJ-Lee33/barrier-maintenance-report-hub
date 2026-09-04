package com.example.maintenance.domain.analytics;

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

import com.jayway.jsonpath.JsonPath;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AnalyticsControllerTest {

	private static final String TECH_EMAIL = "tech01@example.com";
	private static final String MANAGER_EMAIL = "manager01@example.com";
	private static final String PASSWORD = "password1234";
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("MANAGER는 전체 오류 유형 통계를 조회할 수 있다")
	void getErrorTypeStatistics_success_byManager() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		createApprovedReport(techToken, managerToken);

		mockMvc.perform(get("/api/analytics/error-types")
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].errorTypeId").exists())
			.andExpect(jsonPath("$[0].code").exists())
			.andExpect(jsonPath("$[0].name").exists())
			.andExpect(jsonPath("$[0].count").exists());
	}

	@Test
	@DisplayName("MANAGER는 특정 장비 분석 통계를 조회할 수 있다")
	void getDeviceAnalytics_success_byManager() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		createApprovedReport(techToken, managerToken);

		mockMvc.perform(get("/api/analytics/devices/{deviceId}", 1)
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.deviceId").value(1))
			.andExpect(jsonPath("$.serialNo").exists())
			.andExpect(jsonPath("$.location").exists())
			.andExpect(jsonPath("$.totalReportCount").exists())
			.andExpect(jsonPath("$.errorTypeStats").isArray());
	}

	@Test
	@DisplayName("MANAGER는 전체 장비 분석 요약을 조회할 수 있다")
	void getDeviceAnalyticsSummaries_success_byManager() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		createApprovedReport(techToken, managerToken);

		mockMvc.perform(get("/api/analytics/devices")
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].deviceId").exists())
			.andExpect(jsonPath("$[0].serialNo").exists())
			.andExpect(jsonPath("$[0].location").exists())
			.andExpect(jsonPath("$[0].totalReportCount").exists());
	}

	@Test
	@DisplayName("TECHNICIAN은 분석 API에 접근할 수 없다")
	void getAnalytics_fail_byTechnician() throws Exception {
		String techToken = login(TECH_EMAIL);

		mockMvc.perform(get("/api/analytics/devices")
				.header("Authorization", bearer(techToken)))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("존재하지 않는 장비 분석 조회 시 404를 반환한다")
	void getDeviceAnalytics_fail_notFoundDevice() throws Exception {
		String managerToken = login(MANAGER_EMAIL);

		mockMvc.perform(get("/api/analytics/devices/{deviceId}", 99999)
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("장비를 찾을 수 없습니다."));
	}

	private Integer createApprovedReport(
		String techToken,
		String managerToken
	) throws Exception {
		Integer reportId = createReport(techToken);
		submitReport(techToken, reportId);
		approveReport(managerToken, reportId);

		return reportId;
	}

	private Integer createReport(String techToken) throws Exception {
		String unique = unique();

		String response = mockMvc.perform(post("/api/repair-reports")
				.header("Authorization", bearer(techToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "deviceId": 1,
					  "errorTypeIds": [1, 3],
					  "title": "분석 테스트 리포트 %s",
					  "description": "분석 테스트용 리포트입니다.",
					  "repairAction": "센서 케이블 재연결 및 차단봉 동작 확인",
					  "occurredAt": "2026-09-04T19:00:00",
					  "repairedAt": "2026-09-04T20:00:00"
					}
					""".formatted(unique)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(response, "$.id");
	}

	private void submitReport(String techToken, Integer reportId) throws Exception {
		mockMvc.perform(patch("/api/repair-reports/{reportId}/submit", reportId)
				.header("Authorization", bearer(techToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "분석 테스트 제출"
					}
					"""))
			.andExpect(status().isOk());
	}

	private void approveReport(String managerToken, Integer reportId) throws Exception {
		mockMvc.perform(patch("/api/repair-reports/{reportId}/approve", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "분석 테스트 승인"
					}
					"""))
			.andExpect(status().isOk());
	}

	private String login(String email) throws Exception {
		String response = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "%s",
					  "password": "%s"
					}
					""".formatted(email, PASSWORD)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(response, "$.accessToken");
	}

	private String bearer(String token) {
		return "Bearer " + token;
	}

	private String unique() {
		return UUID.randomUUID().toString().substring(0, 8);
	}
}