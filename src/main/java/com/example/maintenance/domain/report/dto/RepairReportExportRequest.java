package com.example.maintenance.domain.report.dto;

import com.example.maintenance.domain.export.ExportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairReportExportRequest(
	@Schema(description = "Export 실행자 User ID", example = "2")
	@NotNull(message = "Export 실행자 ID는 필수입니다.")
	Long changedByUserId,

	@Schema(description = "Export 형식", example = "EXCEL")
	@NotNull(message = "Export 형식은 필수입니다.")
	ExportType exportType,

	@Schema(description = "Export 파일 URL", example = "/exports/report-1.xlsx")
	@NotBlank(message = "파일 URL은 필수입니다.")
	@Size(max = 500, message = "파일 URL은 500자 이하여야 합니다.")
	String fileUrl, //TODO: 진짜 엑셀파일 생성은 아직이고.. 나중에 서버가 파일 생성 후에 정말로 fileUrl을 내뱉도록 추가

	@Schema(description = "Export 사유", example = "클라이언트 제출용 엑셀 변환")
	@Size(max = 500, message = "사유는 500자 이하여야 합니다.")
	String reason
) {
}