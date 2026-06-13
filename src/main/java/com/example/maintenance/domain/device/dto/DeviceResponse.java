package com.example.maintenance.domain.device.dto;

import java.time.LocalDateTime;

import com.example.maintenance.domain.device.Device;

/* 응답 필드만 통제하기 위한 DTO */

public record DeviceResponse(
	Long id,
	String serialNo,
	String location,
	String modelName,
	LocalDateTime installedAt
) {

	public static DeviceResponse from(Device device) {
		return new DeviceResponse(
			device.getId(),
			device.getSerialNo(),
			device.getLocation(),
			device.getModelName(),
			device.getInstalledAt()
		);
	}
}