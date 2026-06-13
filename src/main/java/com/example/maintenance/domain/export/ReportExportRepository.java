package com.example.maintenance.domain.export;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportExportRepository extends JpaRepository<ReportExport, Long> {
	// 특정 리포트의 내보내기 이력 조회
	List<ReportExport> findAllByRepairReportIdOrderByExportedAtDesc(Long repairReportId);
}