package com.example.maintenance.domain.export;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.export.dto.ExternalReportExportResponse;
import com.example.maintenance.domain.report.RepairReport;
import com.example.maintenance.domain.report.ReportErrorTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportMappingService {

	private static final DateTimeFormatter EXPORT_DATE_TIME_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private final ReportErrorTypeRepository reportErrorTypeRepository;

	public ExternalReportExportResponse toExternalReport(RepairReport repairReport) {
		String errorTypeNames = reportErrorTypeRepository
			.findAllByRepairReportId(repairReport.getId())
			.stream()
			.map(reportErrorType -> reportErrorType.getErrorType().getName())
			.collect(Collectors.joining(", "));

		return new ExternalReportExportResponse(
			repairReport.getTechnician().getUser().getName(),
			repairReport.getDevice().getSerialNo(),
			repairReport.getDevice().getLocation(),
			errorTypeNames,
			repairReport.getDescription(),
			repairReport.getRepairAction(),
			formatDateTime(repairReport.getOccurredAt()),
			formatDateTime(repairReport.getRepairedAt())
		);
	}

	private String formatDateTime(LocalDateTime dateTime) {
		if (dateTime == null) {
			return "";
		}

		return dateTime.format(EXPORT_DATE_TIME_FORMATTER);
	}
}