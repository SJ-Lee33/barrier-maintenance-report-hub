package com.example.maintenance.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportStatusChangeRequest(

	@Schema(description = "상태 변경자 User ID", example = "2")
	@NotNull(message = "상태 변경자 ID는 필수입니다.")
	Long changedByUserId, // TODO: JWT 구현했으면 이렇게 하지말고 토큰에서 사용자를 꺼내야 됨

	@Schema(description = "상태 변경 사유", example = "관리팀 검토 후 승인")
	@Size(max = 500, message = "사유는 500자 이하여야 합니다.")
	String reason
) {
}