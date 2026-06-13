package com.example.maintenance.domain.report.dto;

import com.example.maintenance.domain.export.ExportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairReportExportRequest(

	@NotNull(message = "Export 실행자 ID는 필수입니다.")
	Long changedByUserId,

	@NotNull(message = "Export 형식은 필수입니다.")
	ExportType exportType,

	@NotBlank(message = "파일 URL은 필수입니다.")
	@Size(max = 500, message = "파일 URL은 500자 이하여야 합니다.")
	String fileUrl, //TODO: 진짜 엑셀파일 생성은 아직이고.. 나중에 서버가 파일 생성 후에 정말로 fileUrl을 내뱉도록 추가

	@Size(max = 500, message = "사유는 500자 이하여야 합니다.")
	String reason
) {
}