package com.example.maintenance.domain.image;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {

	List<ReportImage> findAllByRepairReportIdOrderByUploadedAtAsc(Long repairReportId);

	Optional<ReportImage> findByIdAndRepairReportId(Long imageId, Long repairReportId);
}