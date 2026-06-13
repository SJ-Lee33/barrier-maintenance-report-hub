package com.example.maintenance.domain.report.dto;

import java.time.LocalDateTime;

import com.example.maintenance.domain.export.ExportType;
import com.example.maintenance.domain.export.ReportExport;

public record ReportExportResponse(
	Long id,
	Long reportId,
	ExportType exportType,
	Long exportedByUserId,
	String exportedByUserName,
	LocalDateTime exportedAt,
	String fileUrl
) {

	public static ReportExportResponse from(ReportExport reportExport) {
		return new ReportExportResponse(
			reportExport.getId(),
			reportExport.getRepairReport().getId(),
			reportExport.getExportType(),
			reportExport.getExportedBy().getId(),
			reportExport.getExportedBy().getName(),
			reportExport.getExportedAt(),
			reportExport.getFileUrl()
		);
	}
}