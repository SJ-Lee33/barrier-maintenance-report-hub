package com.example.maintenance.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전체 장비 분석 요약 응답")
public record DeviceAnalyticsSummaryResponse(

	@Schema(description = "장비 ID", example = "1")
	Long deviceId,

	@Schema(description = "장비 기번", example = "GATE-2024-001")
	String serialNo,

	@Schema(description = "장비 위치", example = "서울 강남구 A주차장 입구")
	String location,

	@Schema(description = "분석 대상 리포트 수", example = "10")
	Long totalReportCount,

	@Schema(description = "가장 많이 발생한 오류 유형 이름", example = "센서 오류")
	String topErrorTypeName,

	@Schema(description = "가장 많이 발생한 오류 유형 발생 횟수", example = "4")
	Long topErrorTypeCount,

	@Schema(description = "가장 많이 발생한 오류 유형 재발률", example = "40.0")
	Double topErrorTypeRecurrenceRate
) {
}