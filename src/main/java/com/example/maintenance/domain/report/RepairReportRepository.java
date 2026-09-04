package com.example.maintenance.domain.report;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.maintenance.domain.analytics.projection.DeviceReportCountProjection;

public interface RepairReportRepository extends JpaRepository<RepairReport, Long> {

	List<RepairReport> findAllByDeletedFalse();

	Optional<RepairReport> findByIdAndDeletedFalse(Long id);

	List<RepairReport> findAllByTechnicianUserIdAndDeletedFalse(Long userId);

	Optional<RepairReport> findByIdAndTechnicianUserIdAndDeletedFalse(Long id, Long userId);

	@Query("""
		select count(rr.id)
		from RepairReport rr
		where rr.deleted = false
		  and rr.device.id = :deviceId
		  and rr.status in (
		  	com.example.maintenance.domain.report.ReportStatus.APPROVED,
		  	com.example.maintenance.domain.report.ReportStatus.EXPORTED
		  )
		""")
	Long countReportsByDeviceIdForAnalytics(Long deviceId);

	@Query("""
		select
			d.id as deviceId,
			d.serialNo as serialNo,
			d.location as location,
			count(rr.id) as totalReportCount
		from RepairReport rr
		join rr.device d
		where rr.deleted = false
		  and rr.status in (
		  	com.example.maintenance.domain.report.ReportStatus.APPROVED,
		  	com.example.maintenance.domain.report.ReportStatus.EXPORTED
		  )
		group by d.id, d.serialNo, d.location
		order by count(rr.id) desc
		""")
	List<DeviceReportCountProjection> countReportsByDevicesForAnalytics();
}