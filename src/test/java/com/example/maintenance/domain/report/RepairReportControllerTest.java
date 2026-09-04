package com.example.maintenance.domain.report;

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
class RepairReportControllerTest {

	private static final String TECH_EMAIL = "tech3@example.com";
	private static final String MANAGER_EMAIL = "manager1@example.com";
	private static final String PASSWORD = "password1234";

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("기사는 리포트를 생성하고 제출할 수 있다")
	void createAndSubmitReport_success() throws Exception {
		String techToken = login(TECH_EMAIL);

		Integer reportId = createReport(techToken);

		mockMvc.perform(patch("/api/repair-reports/{reportId}/submit", reportId)
				.header("Authorization", bearer(techToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "리포트 제출 테스트"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("SUBMITTED"))
			.andExpect(jsonPath("$.submittedByUserName").exists());
	}

	@Test
	@DisplayName("관리자는 제출된 리포트를 승인할 수 있다")
	void approveReport_success_byManager() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createReport(techToken);
		submitReport(techToken, reportId);

		mockMvc.perform(patch("/api/repair-reports/{reportId}/approve", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "관리자 승인 테스트"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("APPROVED"))
			.andExpect(jsonPath("$.approvedByUserName").exists());
	}

	@Test
	@DisplayName("관리자는 제출된 리포트를 반려할 수 있고 기사는 재제출할 수 있다")
	void rejectAndResubmitReport_success() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createReport(techToken);
		submitReport(techToken, reportId);

		mockMvc.perform(patch("/api/repair-reports/{reportId}/reject", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "보완 필요"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("REJECTED"));

		mockMvc.perform(patch("/api/repair-reports/{reportId}/resubmit", reportId)
				.header("Authorization", bearer(techToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "보완 후 재제출"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("RESUBMITTED"));
	}

	@Test
	@DisplayName("승인 전 리포트는 Export할 수 없다")
	void exportReport_fail_beforeApproved() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createReport(techToken);

		mockMvc.perform(post("/api/repair-reports/{reportId}/export", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "exportType": "JSON",
					  "reason": "승인 전 Export 시도"
					}
					"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("기사 권한으로 승인할 수 없다")
	void approveReport_fail_byTechnician() throws Exception {
		String techToken = login(TECH_EMAIL);

		Integer reportId = createReport(techToken);
		submitReport(techToken, reportId);

		mockMvc.perform(patch("/api/repair-reports/{reportId}/approve", reportId)
				.header("Authorization", bearer(techToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "기사 권한 승인 시도"
					}
					"""))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("인증 없이 리포트를 생성할 수 없다")
	void createReport_fail_withoutToken() throws Exception {
		mockMvc.perform(post("/api/repair-reports")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "deviceId": 1,
					  "errorTypeIds": [1, 3],
					  "title": "인증 실패 테스트",
					  "description": "인증 없이 생성 시도",
					  "repairAction": "점검",
					  "occurredAt": "2026-09-04T19:00:00",
					  "repairedAt": "2026-09-04T20:00:00"
					}
					"""))
			.andExpect(status().isForbidden());
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
					  "title": "상태 변경 테스트 리포트 %s",
					  "description": "상태 변경 테스트용 리포트입니다.",
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
					  "reason": "제출 테스트"
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