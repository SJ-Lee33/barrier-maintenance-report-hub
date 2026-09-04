package com.example.maintenance.domain.report.dto;

import com.example.maintenance.domain.export.ExportType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairReportExportRequest(

	@Schema(description = "Export 형식", example = "EXCEL")
	@NotNull(message = "Export 형식은 필수입니다.")
	ExportType exportType,

	@Schema(description = "Export 사유", example = "클라이언트 제출용 엑셀 변환")
	@Size(max = 500, message = "사유는 500자 이하여야 합니다.")
	String reason
) {
}