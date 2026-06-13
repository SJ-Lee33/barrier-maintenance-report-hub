package com.example.maintenance.domain.report.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record RepairReportUpdateRequest(

	@Size(max = 100, message = "제목은 100자 이하여야 합니다.")
	String title,

	@Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
	String description,

	@Size(max = 2000, message = "수리 조치는 2000자 이하여야 합니다.")
	String repairAction,

	LocalDateTime occurredAt,

	LocalDateTime repairedAt

	//TODO: errorTypeIds 수정도 추가해야됨
) {
}