package com.example.maintenance.domain.analytics.projection;

public interface DeviceReportCountProjection {

	Long getDeviceId();

	String getSerialNo();

	String getLocation();

	Long getTotalReportCount();
}