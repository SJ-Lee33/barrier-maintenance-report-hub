package com.example.maintenance.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RepairReportCreateRequest(

	@NotNull(message = "기사 ID는 필수입니다.")
	Long technicianId,

	@NotNull(message = "장비 ID는 필수입니다.")
	Long deviceId,

	@NotEmpty(message = "오류 유형은 최소 1개 이상 선택해야 합니다.")
	List<Long> errorTypeIds,

	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 100, message = "제목은 100자 이하여야 합니다.")
	String title,

	@NotBlank(message = "설명은 필수입니다.")
	@Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
	String description,

	@Size(max = 2000, message = "수리 조치는 2000자 이하여야 합니다.")
	String repairAction,

	@NotNull(message = "발생 시각은 필수입니다.")
	LocalDateTime occurredAt,

	LocalDateTime repairedAt
) {
}