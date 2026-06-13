package com.example.maintenance.domain.report;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.report.dto.RepairReportCreateRequest;
import com.example.maintenance.domain.report.dto.RepairReportExportRequest;
import com.example.maintenance.domain.report.dto.RepairReportResponse;
import com.example.maintenance.domain.report.dto.RepairReportUpdateRequest;
import com.example.maintenance.domain.report.dto.ReportExportResponse;
import com.example.maintenance.domain.report.dto.ReportStatusChangeRequest;
import com.example.maintenance.domain.report.dto.ReportStatusHistoryResponse;
import com.example.maintenance.global.error.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/repair-reports")
@Tag(name = "Repair Report API", description = "유지보수 리포트 등록, 조회, 수정, 상태 변경, 내보내기 API")
public class RepairReportController {

	private final RepairReportService repairReportService;

	@Operation(summary = "리포트 생성", description = "현장 기사가 유지보수 리포트를 DRAFT 상태로 생성합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "리포트 생성 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "요청 값 검증 실패 또는 존재하지 않는 오류 유형 포함",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "기사 또는 장비를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		)
	})
	@PostMapping
	public ResponseEntity<RepairReportResponse> createRepairReport(
		@Valid @RequestBody RepairReportCreateRequest request
	) {
		RepairReportResponse response = repairReportService.createRepairReport(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(response);
	}

	@Operation(summary = "리포트 전체 조회", description = "삭제되지 않은 유지보수 리포트 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<List<RepairReportResponse>> getRepairReports() {
		List<RepairReportResponse> responses = repairReportService.getRepairReports();

		return ResponseEntity.ok(responses);
	}

	@Operation(summary = "리포트 단건 조회", description = "리포트 ID로 유지보수 리포트 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "리포트 조회 성공"
		),
		@ApiResponse(
			responseCode = "404",
			description = "리포트를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		)
	})
	@GetMapping("/{reportId}")
	public ResponseEntity<RepairReportResponse> getRepairReport(
		@PathVariable Long reportId
	) {
		RepairReportResponse response = repairReportService.getRepairReport(reportId);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리포트 수정", description = "DRAFT 또는 수정 가능한 리포트의 본문 정보를 수정합니다.")
	@PatchMapping("/{reportId}")
	public ResponseEntity<RepairReportResponse> updateRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody RepairReportUpdateRequest request
	) {
		RepairReportResponse response = repairReportService.updateRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리포트 삭제", description = "리포트를 soft delete 처리합니다.")
	@DeleteMapping("/{reportId}")
	public ResponseEntity<Void> deleteRepairReport(
		@PathVariable Long reportId
	) {
		repairReportService.deleteRepairReport(reportId);

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "리포트 제출", description = "DRAFT 또는 RESUBMITTED 상태의 리포트를 SUBMITTED 상태로 변경하고 제출자와 제출 시각을 기록합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "리포트 제출 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "요청 값 검증 실패 또는 존재하지 않는 오류 유형 포함",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "기사 또는 장비를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		)
	})
	@PatchMapping("/{reportId}/submit")
	public ResponseEntity<RepairReportResponse> submitRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody ReportStatusChangeRequest request
	) {
		RepairReportResponse response = repairReportService.submitRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리포트 승인", description = "SUBMITTED 또는 REVIEWING 상태의 리포트를 APPROVED 상태로 변경하고 승인자와 승인 시각을 기록합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "리포트 승인 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "승인할 수 없는 상태 또는 잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "리포트 또는 상태 변경자를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		)
	})
	@PatchMapping("/{reportId}/approve")
	public ResponseEntity<RepairReportResponse> approveRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody ReportStatusChangeRequest request
	) {
		RepairReportResponse response = repairReportService.approveRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@Operation(
		summary = "리포트 반려",
		description = "SUBMITTED 또는 REVIEWING 상태의 리포트를 REJECTED 상태로 변경하고 반려 사유를 이력에 저장합니다."
	)
	@PatchMapping("/{reportId}/reject")
	public ResponseEntity<RepairReportResponse> rejectRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody ReportStatusChangeRequest request
	) {
		RepairReportResponse response = repairReportService.rejectRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리포트 재제출 준비", description = "REJECTED 상태의 리포트를 RESUBMITTED 상태로 변경합니다.")
	@PatchMapping("/{reportId}/resubmit")
	public ResponseEntity<RepairReportResponse> resubmitRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody ReportStatusChangeRequest request
	) {
		RepairReportResponse response = repairReportService.resubmitRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리포트 검토 시작", description = "SUBMITTED 상태의 리포트를 REVIEWING 상태로 변경합니다.")
	@PatchMapping("/{reportId}/review")
	public ResponseEntity<RepairReportResponse> reviewingRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody ReportStatusChangeRequest request
	) {
		RepairReportResponse response = repairReportService.reviewingRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리포트 내보내기", description = "APPROVED 상태의 리포트를 외부 보고 양식으로 내보내고 export 이력을 저장합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "리포트 내보내기 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "요청 값 검증 실패 또는 존재하지 않는 오류 유형 포함",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "기사 또는 장비를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		)
	})
	@PostMapping("/{reportId}/export")
	public ResponseEntity<RepairReportResponse> exportRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody RepairReportExportRequest request
	) {
		RepairReportResponse response = repairReportService.exportRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "상태 변경 이력 조회", description = "특정 리포트의 상태 변경 이력을 시간순으로 조회합니다.")
	@GetMapping("/{reportId}/histories")
	public ResponseEntity<List<ReportStatusHistoryResponse>> getReportHistories(
		@PathVariable Long reportId
	) {
		List<ReportStatusHistoryResponse> responses =
			repairReportService.getReportHistories(reportId);

		return ResponseEntity.ok(responses);
	}

	@Operation(summary = "내보내기 이력 조회", description = "특정 리포트의 export 파일 생성 이력을 조회합니다.")
	@GetMapping("/{reportId}/exports")
	public ResponseEntity<List<ReportExportResponse>> getReportExports(
		@PathVariable Long reportId
	) {
		List<ReportExportResponse> responses =
			repairReportService.getReportExports(reportId);

		return ResponseEntity.ok(responses);
	}
}