package com.example.maintenance.domain.report;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.device.Device;
import com.example.maintenance.domain.device.DeviceRepository;
import com.example.maintenance.domain.errortype.ErrorType;
import com.example.maintenance.domain.errortype.ErrorTypeRepository;
import com.example.maintenance.domain.history.ReportStatusHistory;
import com.example.maintenance.domain.history.ReportStatusHistoryRepository;
import com.example.maintenance.domain.report.dto.RepairReportCreateRequest;
import com.example.maintenance.domain.report.dto.RepairReportResponse;
import com.example.maintenance.domain.report.dto.RepairReportUpdateRequest;
import com.example.maintenance.domain.report.dto.ReportErrorTypeResponse;
import com.example.maintenance.domain.report.dto.ReportStatusChangeRequest;
import com.example.maintenance.domain.technician.Technician;
import com.example.maintenance.domain.technician.TechnicianRepository;
import com.example.maintenance.domain.user.User;
import com.example.maintenance.domain.user.UserRepository;

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

	@Transactional
	public RepairReportResponse createRepairReport(RepairReportCreateRequest request) {
		Technician technician = technicianRepository.findById(request.technicianId())
			.orElseThrow(() -> new IllegalArgumentException("기사를 찾을 수 없습니다."));

		Device device = deviceRepository.findById(request.deviceId())
			.orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다."));

		List<ErrorType> errorTypes = errorTypeRepository.findAllById(request.errorTypeIds());

		if (errorTypes.size() != request.errorTypeIds().size()) {
			throw new IllegalArgumentException("존재하지 않는 오류 유형이 포함되어 있습니다.");
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
			.orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다."));

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse updateRepairReport(
		Long reportId,
		RepairReportUpdateRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다."));

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
			.orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다."));

		repairReport.delete();
	}

	@Transactional
	public RepairReportResponse submitRepairReport(
		Long reportId,
		ReportStatusChangeRequest request
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new IllegalArgumentException("상태 변경자를 찾을 수 없습니다."));

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
			.orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new IllegalArgumentException("상태 변경자를 찾을 수 없습니다."));

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
			.orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다."));

		User changedBy = userRepository.findById(request.changedByUserId())
			.orElseThrow(() -> new IllegalArgumentException("상태 변경자를 찾을 수 없습니다."));

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
	
	private RepairReportResponse toResponse(RepairReport repairReport) {
		List<ReportErrorType> reportErrorTypes =
			reportErrorTypeRepository.findAllByRepairReportId(repairReport.getId());

		List<ReportErrorTypeResponse> errorTypeResponses = reportErrorTypes.stream()
			.map(ReportErrorTypeResponse::from)
			.toList();

		return RepairReportResponse.of(repairReport, errorTypeResponses);
	}
}