package com.example.maintenance.domain.report.dto;

import com.example.maintenance.domain.report.ReportErrorType;

public record ReportErrorTypeResponse(
	Long id,
	String code,
	String name,
	String description
) {

	public static ReportErrorTypeResponse from(ReportErrorType reportErrorType) {
		return new ReportErrorTypeResponse(
			reportErrorType.getErrorType().getId(),
			reportErrorType.getErrorType().getCode(),
			reportErrorType.getErrorType().getName(),
			reportErrorType.getErrorType().getDescription()
		);
	}
}