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
	String fileUrl,
	String downloadUrl,
	String fileName
) {

	public static ReportExportResponse from(ReportExport reportExport) {
		String fileUrl = reportExport.getFileUrl();

		return new ReportExportResponse(
			reportExport.getId(),
			reportExport.getRepairReport().getId(),
			reportExport.getExportType(),
			reportExport.getExportedBy().getId(),
			reportExport.getExportedBy().getName(),
			reportExport.getExportedAt(),
			fileUrl,
			createDownloadUrl(fileUrl),
			extractFileName(fileUrl)
		);
	}

	private static String createDownloadUrl(String fileUrl) {
		if (fileUrl == null || fileUrl.isBlank()) {
			return "";
		}

		return "http://localhost:8080" + fileUrl;
	}

	private static String extractFileName(String fileUrl) {
		if (fileUrl == null || fileUrl.isBlank()) {
			return "";
		}

		int lastSlashIndex = fileUrl.lastIndexOf("/");

		if (lastSlashIndex == -1) {
			return fileUrl;
		}

		return fileUrl.substring(lastSlashIndex + 1);
	}
}