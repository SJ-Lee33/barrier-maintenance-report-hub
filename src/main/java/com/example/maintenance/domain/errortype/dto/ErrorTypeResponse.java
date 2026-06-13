package com.example.maintenance.domain.errortype.dto;

import com.example.maintenance.domain.errortype.ErrorType;

public record ErrorTypeResponse(
	Long id,
	String code,
	String name,
	String description
) {

	public static ErrorTypeResponse from(ErrorType errorType) {
		return new ErrorTypeResponse(
			errorType.getId(),
			errorType.getCode(),
			errorType.getName(),
			errorType.getDescription()
		);
	}
}