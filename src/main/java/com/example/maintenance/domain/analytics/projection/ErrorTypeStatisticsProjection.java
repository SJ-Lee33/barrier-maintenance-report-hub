package com.example.maintenance.domain.analytics.projection;

public interface ErrorTypeStatisticsProjection {

	Long getErrorTypeId();

	String getCode();

	String getName();

	Long getCount();
}