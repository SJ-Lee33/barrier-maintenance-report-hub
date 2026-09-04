package com.example.maintenance.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오류 유형별 통계 응답")
public record ErrorTypeStatisticsResponse(

	@Schema(description = "오류 유형 ID", example = "1")
	Long errorTypeId,

	@Schema(description = "오류 유형 코드", example = "SENSOR_ERROR")
	String code,

	@Schema(description = "오류 유형 이름", example = "센서 오류")
	String name,

	@Schema(description = "발생 횟수", example = "4")
	Long count,

	@Schema(description = "재발률", example = "40.0")
	Double recurrenceRate
) {
}