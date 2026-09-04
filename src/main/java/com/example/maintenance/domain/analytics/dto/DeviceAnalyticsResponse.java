package com.example.maintenance.domain.analytics.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 장비 분석 응답")
public record DeviceAnalyticsResponse(

	@Schema(description = "장비 ID", example = "1")
	Long deviceId,

	@Schema(description = "장비 기번", example = "GATE-2024-001")
	String serialNo,

	@Schema(description = "장비 위치", example = "서울 강남구 A주차장 입구")
	String location,

	@Schema(description = "분석 대상 리포트 수", example = "10")
	Long totalReportCount,

	@Schema(description = "오류 유형별 통계 목록")
	List<ErrorTypeStatisticsResponse> errorTypeStats
) {
}