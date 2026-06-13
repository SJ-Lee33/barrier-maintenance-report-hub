package com.example.maintenance.domain.errortype;

public enum ErrorTypeCode {

	SENSOR_ERROR("센서 오류", "차량 감지 센서 인식 불량 또는 오작동"),
	POWER_FAILURE("전원 장애", "전원 공급 불량, 차단기 전원 이상"),
	ARM_STUCK("차단봉 걸림", "차단봉이 정상적으로 올라가거나 내려가지 않는 문제"),
	UNKNOWN("알 수 없음", "아직 원인을 알 수 없는 오류");

	private final String displayName;
	private final String description;

	ErrorTypeCode(String displayName, String description) {
		this.displayName = displayName;
		this.description = description;
	}

	public String getCode() {
		return name();
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}
}