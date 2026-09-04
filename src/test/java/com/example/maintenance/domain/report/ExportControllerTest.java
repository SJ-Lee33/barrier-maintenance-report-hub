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
class ExportControllerTest {

	private static final String TECH_EMAIL = "tech3@example.com";
	private static final String MANAGER_EMAIL = "manager1@example.com";
	private static final String PASSWORD = "password1234";

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("관리자는 승인된 리포트를 JSON 파일로 Export할 수 있다")
	void exportJson_success() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createApprovedReport(techToken, managerToken);

		mockMvc.perform(post("/api/repair-reports/{reportId}/export", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "exportType": "JSON",
					  "reason": "JSON Export 테스트"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("EXPORTED"))
			.andExpect(jsonPath("$.exportedByUserName").exists());

		mockMvc.perform(get("/api/repair-reports/{reportId}/exports", reportId)
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].exportType").value("JSON"))
			.andExpect(jsonPath("$[0].fileUrl").value(org.hamcrest.Matchers.endsWith(".json")))
			.andExpect(jsonPath("$[0].downloadUrl").exists())
			.andExpect(jsonPath("$[0].fileName").value(org.hamcrest.Matchers.endsWith(".json")));
	}

	@Test
	@DisplayName("관리자는 승인된 리포트를 CSV 파일로 Export할 수 있다")
	void exportCsv_success() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createApprovedReport(techToken, managerToken);

		mockMvc.perform(post("/api/repair-reports/{reportId}/export", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "exportType": "CSV",
					  "reason": "CSV Export 테스트"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("EXPORTED"))
			.andExpect(jsonPath("$.exportedByUserName").exists());

		mockMvc.perform(get("/api/repair-reports/{reportId}/exports", reportId)
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].exportType").value("CSV"))
			.andExpect(jsonPath("$[0].fileUrl").value(org.hamcrest.Matchers.endsWith(".csv")))
			.andExpect(jsonPath("$[0].downloadUrl").exists())
			.andExpect(jsonPath("$[0].fileName").value(org.hamcrest.Matchers.endsWith(".csv")));
	}

	@Test
	@DisplayName("관리자는 승인된 리포트를 Excel 파일로 Export할 수 있다")
	void exportExcel_success() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createApprovedReport(techToken, managerToken);

		mockMvc.perform(post("/api/repair-reports/{reportId}/export", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "exportType": "EXCEL",
					  "reason": "Excel Export 테스트"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("EXPORTED"))
			.andExpect(jsonPath("$.exportedByUserName").exists());

		mockMvc.perform(get("/api/repair-reports/{reportId}/exports", reportId)
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].exportType").value("EXCEL"))
			.andExpect(jsonPath("$[0].fileUrl").value(org.hamcrest.Matchers.endsWith(".xlsx")))
			.andExpect(jsonPath("$[0].downloadUrl").exists())
			.andExpect(jsonPath("$[0].fileName").value(org.hamcrest.Matchers.endsWith(".xlsx")));
	}

	@Test
	@DisplayName("EXPORTED 상태 리포트는 여러 포맷으로 재Export할 수 있다")
	void reExport_success() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createApprovedReport(techToken, managerToken);

		exportReport(managerToken, reportId, "JSON");
		exportReport(managerToken, reportId, "CSV");
		exportReport(managerToken, reportId, "EXCEL");

		mockMvc.perform(get("/api/repair-reports/{reportId}/exports", reportId)
				.header("Authorization", bearer(managerToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(3));
	}

	@Test
	@DisplayName("기사 권한으로는 Export할 수 없다")
	void export_fail_byTechnician() throws Exception {
		String techToken = login(TECH_EMAIL);
		String managerToken = login(MANAGER_EMAIL);

		Integer reportId = createApprovedReport(techToken, managerToken);

		mockMvc.perform(post("/api/repair-reports/{reportId}/export", reportId)
				.header("Authorization", bearer(techToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "exportType": "JSON",
					  "reason": "기사 Export 시도"
					}
					"""))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("지원하지 않는 Export 형식이면 400 응답을 반환한다")
	void export_fail_invalidExportType() throws Exception {
		String managerToken = login(MANAGER_EMAIL);

		mockMvc.perform(post("/api/repair-reports/{reportId}/export", 12)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "exportType": "PDF",
					  "reason": "잘못된 Export 형식"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value("지원하지 않는 Export 형식입니다. exportType은 JSON, CSV, EXCEL 중 하나여야 합니다."));
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
					  "title": "Export 테스트 리포트 %s",
					  "description": "Export 테스트용 리포트입니다.",
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

	private void approveReport(String managerToken, Integer reportId) throws Exception {
		mockMvc.perform(patch("/api/repair-reports/{reportId}/approve", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "reason": "승인 테스트"
					}
					"""))
			.andExpect(status().isOk());
	}

	private void exportReport(
		String managerToken,
		Integer reportId,
		String exportType
	) throws Exception {
		mockMvc.perform(post("/api/repair-reports/{reportId}/export", reportId)
				.header("Authorization", bearer(managerToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "exportType": "%s",
					  "reason": "%s Export 테스트"
					}
					""".formatted(exportType, exportType)))
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