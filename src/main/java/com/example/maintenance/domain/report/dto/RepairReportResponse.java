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
	LocalDateTime repairedAt,

	LocalDateTime submittedAt,
	Long submittedByUserId,
	String submittedByUserName,

	LocalDateTime approvedAt,
	Long approvedByUserId,
	String approvedByUserName,

	LocalDateTime exportedAt,
	Long exportedByUserId,
	String exportedByUserName
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
			repairReport.getRepairedAt(),

			repairReport.getSubmittedAt(),
			repairReport.getSubmittedBy() == null ? null : repairReport.getSubmittedBy().getId(),
			repairReport.getSubmittedBy() == null ? null : repairReport.getSubmittedBy().getName(),

			repairReport.getApprovedAt(),
			repairReport.getApprovedBy() == null ? null : repairReport.getApprovedBy().getId(),
			repairReport.getApprovedBy() == null ? null : repairReport.getApprovedBy().getName(),

			repairReport.getExportedAt(),
			repairReport.getExportedBy() == null ? null : repairReport.getExportedBy().getId(),
			repairReport.getExportedBy() == null ? null : repairReport.getExportedBy().getName()
		);
	}
}