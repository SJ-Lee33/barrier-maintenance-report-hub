package com.example.maintenance.domain.report.dto;

import java.time.LocalDateTime;

import com.example.maintenance.domain.history.ReportStatusHistory;
import com.example.maintenance.domain.report.ReportStatus;

public record ReportStatusHistoryResponse(
	Long id,
	Long reportId,
	ReportStatus fromStatus,
	ReportStatus toStatus,
	Long changedByUserId,
	String changedByUserName,
	String reason,
	LocalDateTime changedAt
) {

	public static ReportStatusHistoryResponse from(ReportStatusHistory history) {
		return new ReportStatusHistoryResponse(
			history.getId(),
			history.getRepairReport().getId(),
			history.getFromStatus(),
			history.getToStatus(),
			history.getChangedBy().getId(),
			history.getChangedBy().getName(),
			history.getReason(),
			history.getChangedAt()
		);
	}
}