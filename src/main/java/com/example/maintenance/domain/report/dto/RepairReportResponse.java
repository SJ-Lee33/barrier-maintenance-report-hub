package com.example.maintenance.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.maintenance.domain.report.RepairReport;
import com.example.maintenance.domain.report.ReportStatus;

public record RepairReportResponse(
	Long id,
	Long technicianId,
	String technicianName,
	Long deviceId,
	String deviceSerialNo,
	List<ReportErrorTypeResponse> errorTypes,
	String title,
	String description,
	String repairAction,
	ReportStatus status,
	LocalDateTime occurredAt,
	LocalDateTime repairedAt
) {

	public static RepairReportResponse of(
		RepairReport repairReport,
		List<ReportErrorTypeResponse> errorTypes
	) {
		return new RepairReportResponse(
			repairReport.getId(),
			repairReport.getTechnician().getId(),
			repairReport.getTechnician().getUser().getName(),
			repairReport.getDevice().getId(),
			repairReport.getDevice().getSerialNo(),
			errorTypes,
			repairReport.getTitle(),
			repairReport.getDescription(),
			repairReport.getRepairAction(),
			repairReport.getStatus(),
			repairReport.getOccurredAt(),
			repairReport.getRepairedAt()
		);
	}
}