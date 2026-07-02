package com.example.maintenance.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record ReportStatusChangeRequest(

	@Schema(description = "상태 변경 사유", example = "관리팀 검토 후 승인")
	@Size(max = 500, message = "사유는 500자 이하여야 합니다.")
	String reason
) {
}