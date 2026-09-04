package com.example.maintenance.domain.report;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.device.Device;
import com.example.maintenance.domain.device.DeviceRepository;
import com.example.maintenance.domain.errortype.ErrorType;
import com.example.maintenance.domain.errortype.ErrorTypeRepository;
import com.example.maintenance.domain.export.ExportMappingService;
import com.example.maintenance.domain.export.ExportType;
import com.example.maintenance.domain.export.JsonExportFileGenerator;
import com.example.maintenance.domain.export.ReportExport;
import com.example.maintenance.domain.export.ReportExportRepository;
import com.example.maintenance.domain.export.dto.ExternalReportExportResponse;
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
import com.example.maintenance.global.error.ForbiddenException;
import com.example.maintenance.global.error.NotFoundException;
import com.example.maintenance.global.storage.ExportFileStorage;
import com.example.maintenance.global.storage.ExportFileStorage.ExportFilePath;

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
	private final ReportExportRepository reportExportRepository;

	@Transactional
	public RepairReportResponse createRepairReport(
		RepairReportCreateRequest request,
		User currentUser
	) {
		Technician technician = technicianRepository.findByUserId(currentUser.getId())
			.orElseThrow(() -> new NotFoundException("현재 로그인한 사용자와 연결된 기사 프로필을 찾을 수 없습니다."));

		Device device = deviceRepository.findById(request.deviceId())
			.orElseThrow(() -> new NotFoundException("장비를 찾을 수 없습니다."));

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

		return toResponse(savedRepairReport);
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

	// 본인이 생성한 리포트만 수정 / 삭제 / 제출 / 재제출 가능
	private void validateReportOwner(RepairReport repairReport, User currentUser) {
		Long reportOwnerUserId = repairReport.getTechnician().getUser().getId();

		if (!reportOwnerUserId.equals(currentUser.getId())) {
			throw new ForbiddenException("본인 리포트만 처리할 수 있습니다.");
		}
	}

	@Transactional
	public RepairReportResponse updateRepairReport(
		Long reportId,
		RepairReportUpdateRequest request,
		User currentUser
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		validateReportOwner(repairReport, currentUser);

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
	public void deleteRepairReport(Long reportId, User currentUser) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		validateReportOwner(repairReport, currentUser);

		repairReport.delete();
	}

	@Transactional
	public RepairReportResponse submitRepairReport(
		Long reportId,
		ReportStatusChangeRequest request,
		User currentUser
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		validateReportOwner(repairReport, currentUser);

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.submit(currentUser);

		reportStatusHistoryRepository.save(
			new ReportStatusHistory(
				repairReport,
				fromStatus,
				repairReport.getStatus(),
				currentUser,
				request.reason()
			)
		);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse approveRepairReport(
		Long reportId,
		ReportStatusChangeRequest request,
		User currentUser
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.approve(currentUser);

		reportStatusHistoryRepository.save(
			new ReportStatusHistory(
				repairReport,
				fromStatus,
				repairReport.getStatus(),
				currentUser,
				request.reason()
			)
		);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse rejectRepairReport(
		Long reportId,
		ReportStatusChangeRequest request,
		User currentUser
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.reject();

		reportStatusHistoryRepository.save(
			new ReportStatusHistory(
				repairReport,
				fromStatus,
				repairReport.getStatus(),
				currentUser,
				request.reason()
			)
		);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse resubmitRepairReport(
		Long reportId,
		ReportStatusChangeRequest request,
		User currentUser
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.resubmit();

		reportStatusHistoryRepository.save(
			new ReportStatusHistory(
				repairReport,
				fromStatus,
				repairReport.getStatus(),
				currentUser,
				request.reason()
			)
		);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse reviewingRepairReport(
		Long reportId,
		ReportStatusChangeRequest request,
		User currentUser
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.startReview();

		reportStatusHistoryRepository.save(
			new ReportStatusHistory(
				repairReport,
				fromStatus,
				repairReport.getStatus(),
				currentUser,
				request.reason()
			)
		);

		return toResponse(repairReport);
	}

	@Transactional
	public RepairReportResponse exportRepairReport(
		Long reportId,
		RepairReportExportRequest request,
		User currentUser
	) {
		RepairReport repairReport = repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));

		ReportStatus fromStatus = repairReport.getStatus();

		repairReport.export(currentUser);

		ExportFilePath exportFilePath = exportFileStorage.createExportFilePath(
			repairReport.getId(),
			request.exportType()
		);

		if (request.exportType() == ExportType.JSON) {
			ExternalReportExportResponse externalReport =
				exportMappingService.toExternalReport(repairReport);

			jsonExportFileGenerator.generate(
				externalReport,
				exportFilePath.filePath()
			);
		} else {
			throw new IllegalArgumentException("아직 지원하지 않는 Export 형식입니다.");
		}

		reportExportRepository.save(
			new ReportExport(
				repairReport,
				request.exportType(),
				currentUser,
				exportFilePath.fileUrl()
			)
		);

		reportStatusHistoryRepository.save(
			new ReportStatusHistory(
				repairReport,
				fromStatus,
				repairReport.getStatus(),
				currentUser,
				request.reason()
			)
		);

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

	// 내보내기
	private final ExportMappingService exportMappingService;
	private final ExportFileStorage exportFileStorage;
	private final JsonExportFileGenerator jsonExportFileGenerator;
}