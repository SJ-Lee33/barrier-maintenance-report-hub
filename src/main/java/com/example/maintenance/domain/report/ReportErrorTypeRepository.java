package com.example.maintenance.domain.report;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportErrorTypeRepository extends JpaRepository<ReportErrorType, Long> {

	List<ReportErrorType> findAllByRepairReportId(Long repairReportId);
}