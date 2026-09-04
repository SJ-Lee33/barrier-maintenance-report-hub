package com.example.maintenance.domain.analytics;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.analytics.dto.DeviceAnalyticsResponse;
import com.example.maintenance.domain.analytics.dto.DeviceAnalyticsSummaryResponse;
import com.example.maintenance.domain.analytics.dto.ErrorTypeStatisticsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Analytics", description = "유지보수 리포트 분석 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	@Operation(
		summary = "전체 오류 유형별 발생 횟수 조회",
		description = """
			분석 대상 리포트를 기준으로 오류 유형별 발생 횟수를 조회합니다.
			
			분석 대상 리포트 기준:
			- 삭제되지 않은 리포트
			- APPROVED 또는 EXPORTED 상태의 리포트
			
			MANAGER, ADMIN 권한 사용자만 호출할 수 있습니다.
			"""
	)
	@GetMapping("/error-types")
	public List<ErrorTypeStatisticsResponse> getErrorTypeStatistics() {
		return analyticsService.getErrorTypeStatistics();
	}

	@Operation(
		summary = "특정 장비 분석 통계 조회",
		description = """
			특정 장비의 분석 대상 리포트 수, 오류 유형별 발생 횟수, 재발률을 조회합니다.
			
			재발률 계산식:
			오류 유형 발생 횟수 / 해당 장비의 총 분석 대상 리포트 수 * 100
			
			재발률은 소수점 첫째 자리까지 반올림합니다.
			MANAGER, ADMIN 권한 사용자만 호출할 수 있습니다.
			"""
	)
	@GetMapping("/devices/{deviceId}")
	public DeviceAnalyticsResponse getDeviceAnalytics(
		@PathVariable Long deviceId
	) {
		return analyticsService.getDeviceAnalytics(deviceId);
	}

	@Operation(
		summary = "전체 장비 분석 요약 조회",
		description = """
			전체 장비별 분석 요약 정보를 조회합니다.
			
			각 장비별로 다음 정보를 반환합니다.
			- 분석 대상 리포트 수
			- 가장 많이 발생한 오류 유형
			- 가장 많이 발생한 오류 유형의 발생 횟수
			- 가장 많이 발생한 오류 유형의 재발률
			
			총 리포트 수 내림차순으로 정렬됩니다.
			MANAGER, ADMIN 권한 사용자만 호출할 수 있습니다.
			"""
	)
	@GetMapping("/devices")
	public List<DeviceAnalyticsSummaryResponse> getDeviceAnalyticsSummaries() {
		return analyticsService.getDeviceAnalyticsSummaries();
	}
}