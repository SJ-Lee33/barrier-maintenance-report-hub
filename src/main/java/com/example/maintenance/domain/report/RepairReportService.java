package com.example.maintenance.domain.report;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.device.Device;
import com.example.maintenance.domain.device.DeviceRepository;
import com.example.maintenance.domain.errortype.ErrorType;
import com.example.maintenance.domain.errortype.ErrorTypeRepository;
import com.example.maintenance.domain.export.ReportExport;
import com.example.maintenance.domain.export.ReportExportRepository;
import com.example.maintenance.domain.history.ReportStatusHistory;
import com.example.maintenance.domain.history.ReportStatusHistoryRepository;
import com.example.maintenance.domain.report.dto.RepairReportCreateRequest;
import com.example.maintenance.domain.report.dto.RepairReportExportRequest;
import com.example.maintenance.domain.report.dto.RepairReportResponse;
import com.example.maintenance.domain.report.dto.RepairReportUpdateRequest;
import com.example.maintenance.domain.report.dto.ReportErrorTypeResponse;
import com.example.maintenance.domain.report.dto.ReportExportResponse;
import com.example.maintenance.domain.report.dto.ReportStatusChangeRequest;
import com.example.maintenance.domain.report.dto.ReportStatusHistoryResponse;
import com.example.maintenance.domain.technician.Technician;
import com.example.maintenance.domain.technician.TechnicianRepository;
import com.example.maintenance.domain.user.User;
import com.example.maintenance.domain.user.UserRepository;
import com.example.maintenance.global.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepairReportService {

	private final RepairReportRepository repairReportRepository;
	private final ReportErrorTypeRepository reportErrorTypeRepository;
	private final TechnicianRepository technicianRepository;
	private final DeviceRepository deviceRepository;
	private final ErrorTypeRepository errorTypeRepository;
	private final ReportStatusHistoryRepository reportStatusHistoryRepository;
	private final UserRepository userRepository;
	private final ReportExportRepository reportExportRepository;

	@Transactional
	public RepairReportResponse createRepairReport(RepairReportCreateRequest request) {
		Technician technician = technicianRepository.findById(request.technicianId())
			.orElseThrow(() -> new NotFoundException("기사를 찾을 수 없습니다."));

		Device device = deviceRepository.findById(request.deviceId())
			.orElseThrow(() -> new NotFoundException("장비를 찾을 수 없습니다."));

		List<ErrorType> errorTypes = errorTypeRepository.findAllById(request.errorTypeIds());

		if (errorTypes.size() != request.errorTypeIds().size()) {
			throw new NotFoundException("존재하지 않는 오류 유형이 포함되어 있습니다.");
		}

		RepairReport repairReport = new RepairReport(
			technician,
			device,
			request.title(),
			request.description(),
			request.repairAction(),
			request.occurredAt(),
			request.repairedAt()
		);

		RepairReport savedRepairReport = repairReportRepository.save(repairReport);

		List<ReportErrorType> reportErrorTypes = errorTypes.stream()
			.map(errorType -> new ReportErrorType(savedRepairReport, errorType))
			.toList();

		reportErrorTypeRepository.saveAll(reportErrorTypes);

		List<ReportErrorTypeResponse> errorTypeResponses = reportErrorTypes.stream()
			.map(ReportErrorTypeResponse::from)
			.toList();

		return RepairReportResponse.of(savedRepairReport, errorTypeResponses);
	}

	public List<RepairReportResponse> getRepairReports() {
		return repairReportRepository.findAllByDeletedFalse()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	public RepairReportResponse getRepairReport(Long reportId) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse updateRepairReport(
		Long reportId,
		RepairReportUpdateRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		repairReport.update(
			request.title(),
			request.description(),
			request.repairAction(),
			request.occurredAt(),
			request.repairedAt()
		);

		return toResponse(repairReport);
	}

	@Transactional
	public void deleteRepairReport(Long reportId) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		repairReport.delete();
	}

	@Transactional
	public RepairReportResponse submitRepairReport(
		Long reportId,
		ReportStatusChangeRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new NotFoundException("상태 변경자를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.submit(changedBy);

		ReportStatusHistory history = new ReportStatusHistory(
			repairReport,
			fromStatus,
			repairReport.getStatus(),
			changedBy,
			request.reason()
		);

		reportStatusHistoryRepository.save(history);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse approveRepairReport(
		Long reportId,
		ReportStatusChangeRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new NotFoundException("상태 변경자를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.approve(changedBy);

		ReportStatusHistory history = new ReportStatusHistory(
			repairReport,
			fromStatus,
			repairReport.getStatus(),
			changedBy,
			request.reason()
		);

		reportStatusHistoryRepository.save(history);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse rejectRepairReport(
		Long reportId,
		ReportStatusChangeRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new NotFoundException("상태 변경자를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.reject();

		ReportStatusHistory history = new ReportStatusHistory(
			repairReport,
			fromStatus,
			repairReport.getStatus(),
			changedBy,
			request.reason()
		);

		reportStatusHistoryRepository.save(history);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse resubmitRepairReport(
		Long reportId,
		ReportStatusChangeRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new NotFoundException("상태 변경자를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.resubmit();

		ReportStatusHistory history = new ReportStatusHistory(
			repairReport,
			fromStatus,
			repairReport.getStatus(),
			changedBy,
			request.reason()
		);

		reportStatusHistoryRepository.save(history);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse reviewingRepairReport(
		Long reportId,
		ReportStatusChangeRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new NotFoundException("상태 변경자를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.reviewing();

		ReportStatusHistory history = new ReportStatusHistory(
			repairReport,
			fromStatus,
			repairReport.getStatus(),
			changedBy,
			request.reason()
		);

		reportStatusHistoryRepository.save(history);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse exportRepairReport(
		Long reportId,
		RepairReportExportRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new NotFoundException("Export 실행자를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.export(changedBy);

		ReportExport reportExport = new ReportExport(
			repairReport,
			request.exportType(),
			changedBy,
			request.fileUrl()
		);

		reportExportRepository.save(reportExport);

		ReportStatusHistory history = new ReportStatusHistory(
			repairReport,
			fromStatus,
			repairReport.getStatus(),
			changedBy,
			request.reason()
		);

		reportStatusHistoryRepository.save(history);

		return toResponse(repairReport);
	}

	private RepairReportResponse toResponse(RepairReport repairReport) {
		List<ReportErrorType> reportErrorTypes =
			reportErrorTypeRepository.findAllByRepairReportId(repairReport.getId());

		List<ReportErrorTypeResponse> errorTypeResponses = reportErrorTypes.stream()
			.map(ReportErrorTypeResponse::from)
			.toList();

		return RepairReportResponse.of(repairReport, errorTypeResponses);
	}

	// 전체 상태변경이력 오름차순 조회
	public List<ReportStatusHistoryResponse> getReportHistories(Long reportId) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		return reportStatusHistoryRepository.findAllByRepairReportIdOrderByChangedAtAsc(repairReport.getId())
			.stream()
			.map(ReportStatusHistoryResponse::from)
			.toList();
	}

	// 내보내기 이력 내림차순 조회
	public List<ReportExportResponse> getReportExports(Long reportId) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		return reportExportRepository.findAllByRepairReportIdOrderByExportedAtDesc(repairReport.getId())
			.stream()
			.map(ReportExportResponse::from)
			.toList();
	}
}