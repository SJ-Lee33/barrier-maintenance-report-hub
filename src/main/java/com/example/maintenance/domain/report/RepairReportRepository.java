package com.example.maintenance.domain.report;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairReportRepository extends JpaRepository<RepairReport, Long> {

	List<RepairReport> findAllByDeletedFalse();

	Optional<RepairReport> findByIdAndDeletedFalse(Long id);

}