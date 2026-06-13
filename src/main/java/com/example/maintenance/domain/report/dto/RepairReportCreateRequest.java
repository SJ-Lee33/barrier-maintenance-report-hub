package com.example.maintenance.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairReportCreateRequest(

	@Schema(description = "기사 ID", example = "1")
	@NotNull(message = "기사 ID는 필수입니다.")
	Long technicianId,

	@Schema(description = "장비 ID", example = "1")
	@NotNull(message = "장비 ID는 필수입니다.")
	Long deviceId,

	@Schema(description = "오류 유형 ID 목록", example = "[1, 3]")
	@NotEmpty(message = "오류 유형은 최소 1개 이상 선택해야 합니다.")
	List<Long> errorTypeIds,

	@Schema(description = "리포트 제목", example = "차단봉 동작 불량")
	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 100, message = "제목은 100자 이하여야 합니다.")
	String title,

	@Schema(description = "고장 설명", example = "차량 진입 시 차단봉이 올라가지 않음")
	@NotBlank(message = "설명은 필수입니다.")
	@Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
	String description,

	@Schema(description = "수리 조치 내용", example = "센서 케이블 재연결 및 동작 확인")
	@Size(max = 2000, message = "수리 조치는 2000자 이하여야 합니다.")
	String repairAction,

	@Schema(description = "고장 발생 시각", example = "2026-06-12T10:00:00")
	@NotNull(message = "발생 시각은 필수입니다.")
	LocalDateTime occurredAt,

	@Schema(description = "수리 완료 시각", example = "2026-06-12T11:00:00")
	LocalDateTime repairedAt
) {
}