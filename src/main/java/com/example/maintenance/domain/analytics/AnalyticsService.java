package com.example.maintenance.domain.analytics;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.analytics.dto.DeviceAnalyticsResponse;
import com.example.maintenance.domain.analytics.dto.DeviceAnalyticsSummaryResponse;
import com.example.maintenance.domain.analytics.dto.ErrorTypeStatisticsResponse;
import com.example.maintenance.domain.analytics.projection.DeviceReportCountProjection;
import com.example.maintenance.domain.analytics.projection.ErrorTypeStatisticsProjection;
import com.example.maintenance.domain.device.Device;
import com.example.maintenance.domain.device.DeviceRepository;
import com.example.maintenance.domain.report.RepairReportRepository;
import com.example.maintenance.domain.report.ReportErrorTypeRepository;
import com.example.maintenance.global.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

	private final DeviceRepository deviceRepository;
	private final RepairReportRepository repairReportRepository;
	private final ReportErrorTypeRepository reportErrorTypeRepository;

	public List<ErrorTypeStatisticsResponse> getErrorTypeStatistics() {
		return reportErrorTypeRepository.countErrorTypesForAnalytics()
			.stream()
			.map(projection -> new ErrorTypeStatisticsResponse(
				projection.getErrorTypeId(),
				projection.getCode(),
				projection.getName(),
				projection.getCount(),
				null
			))
			.toList();
	}

	public DeviceAnalyticsResponse getDeviceAnalytics(Long deviceId) {
		Device device = deviceRepository.findById(deviceId)
			.orElseThrow(() -> new NotFoundException("장비를 찾을 수 없습니다."));

		Long totalReportCount =
			repairReportRepository.countReportsByDeviceIdForAnalytics(deviceId);

		List<ErrorTypeStatisticsResponse> errorTypeStats =
			reportErrorTypeRepository.countErrorTypesByDeviceIdForAnalytics(deviceId)
				.stream()
				.map(projection -> new ErrorTypeStatisticsResponse(
					projection.getErrorTypeId(),
					projection.getCode(),
					projection.getName(),
					projection.getCount(),
					calculateRecurrenceRate(projection.getCount(), totalReportCount)
				))
				.toList();

		return new DeviceAnalyticsResponse(
			device.getId(),
			device.getSerialNo(),
			device.getLocation(),
			totalReportCount,
			errorTypeStats
		);
	}

	public List<DeviceAnalyticsSummaryResponse> getDeviceAnalyticsSummaries() {
		return repairReportRepository.countReportsByDevicesForAnalytics()
			.stream()
			.map(this::toDeviceAnalyticsSummaryResponse)
			.toList();
	}

	private DeviceAnalyticsSummaryResponse toDeviceAnalyticsSummaryResponse(
		DeviceReportCountProjection projection
	) {
		List<ErrorTypeStatisticsProjection> errorTypeStatistics =
			reportErrorTypeRepository.countErrorTypesByDeviceIdForAnalytics(
				projection.getDeviceId()
			);

		ErrorTypeStatisticsProjection topErrorType = errorTypeStatistics
			.stream()
			.max(Comparator.comparingLong(ErrorTypeStatisticsProjection::getCount))
			.orElse(null);

		if (topErrorType == null) {
			return new DeviceAnalyticsSummaryResponse(
				projection.getDeviceId(),
				projection.getSerialNo(),
				projection.getLocation(),
				projection.getTotalReportCount(),
				null,
				0L,
				0.0
			);
		}

		return new DeviceAnalyticsSummaryResponse(
			projection.getDeviceId(),
			projection.getSerialNo(),
			projection.getLocation(),
			projection.getTotalReportCount(),
			topErrorType.getName(),
			topErrorType.getCount(),
			calculateRecurrenceRate(
				topErrorType.getCount(),
				projection.getTotalReportCount()
			)
		);
	}

	private double calculateRecurrenceRate(
		Long errorTypeCount,
		Long totalReportCount
	) {
		if (totalReportCount == null || totalReportCount == 0) {
			return 0.0;
		}

		double rate = (double)errorTypeCount / totalReportCount * 100;

		return Math.round(rate * 10) / 10.0;
	}
}