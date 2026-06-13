package com.example.maintenance.domain.history;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportStatusHistoryRepository extends JpaRepository<ReportStatusHistory, Long> {

	List<ReportStatusHistory> findAllByRepairReportIdOrderByChangedAtAsc(Long repairReportId);
}