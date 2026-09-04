package com.example.maintenance.domain.export.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExternalReportExportResponse(

	@JsonProperty("작업자")
	String technicianName,

	@JsonProperty("장비기번")
	String deviceSerialNo,

	@JsonProperty("장비위치")
	String deviceLocation,

	@JsonProperty("고장유형")
	String errorTypeNames,

	@JsonProperty("고장내용")
	String description,

	@JsonProperty("조치내용")
	String repairAction,

	@JsonProperty("고장발생시각")
	String occurredAt,

	@JsonProperty("수리완료시각")
	String repairedAt
) {
}