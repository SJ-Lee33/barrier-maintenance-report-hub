package com.example.maintenance.domain.report;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.maintenance.domain.analytics.projection.ErrorTypeStatisticsProjection;

public interface ReportErrorTypeRepository extends JpaRepository<ReportErrorType, Long> {

	List<ReportErrorType> findAllByRepairReportId(Long repairReportId);

	@Query("""
		select
			et.id as errorTypeId,
			et.code as code,
			et.name as name,
			count(ret.id) as count
		from ReportErrorType ret
		join ret.errorType et
		join ret.repairReport rr
		where rr.deleted = false
		  and rr.status in (
		  	com.example.maintenance.domain.report.ReportStatus.APPROVED,
		  	com.example.maintenance.domain.report.ReportStatus.EXPORTED
		  )
		group by et.id, et.code, et.name
		order by count(ret.id) desc
		""")
	List<ErrorTypeStatisticsProjection> countErrorTypesForAnalytics();

	@Query("""
		select
			et.id as errorTypeId,
			et.code as code,
			et.name as name,
			count(ret.id) as count
		from ReportErrorType ret
		join ret.errorType et
		join ret.repairReport rr
		where rr.deleted = false
		  and rr.device.id = :deviceId
		  and rr.status in (
		  	com.example.maintenance.domain.report.ReportStatus.APPROVED,
		  	com.example.maintenance.domain.report.ReportStatus.EXPORTED
		  )
		group by et.id, et.code, et.name
		order by count(ret.id) desc
		""")
	List<ErrorTypeStatisticsProjection> countErrorTypesByDeviceIdForAnalytics(Long deviceId);
}