package com.example.maintenance.domain.analytics;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.analytics.dto.DeviceAnalyticsResponse;
import com.example.maintenance.domain.analytics.dto.DeviceAnalyticsSummaryResponse;
import com.example.maintenance.domain.analytics.dto.ErrorTypeStatisticsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	@GetMapping("/error-types")
	public List<ErrorTypeStatisticsResponse> getErrorTypeStatistics() {
		return analyticsService.getErrorTypeStatistics();
	}

	@GetMapping("/devices/{deviceId}")
	public DeviceAnalyticsResponse getDeviceAnalytics(
		@PathVariable Long deviceId
	) {
		return analyticsService.getDeviceAnalytics(deviceId);
	}

	@GetMapping("/devices")
	public List<DeviceAnalyticsSummaryResponse> getDeviceAnalyticsSummaries() {
		return analyticsService.getDeviceAnalyticsSummaries();
	}
}