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
import com.example.maintenance.domain.report.dto.RepairReportResponse;
import com.example.maintenance.domain.report.dto.RepairReportUpdateRequest;
import com.example.maintenance.domain.report.dto.ReportStatusChangeRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/repair-reports")
public class RepairReportController {

	private final RepairReportService repairReportService;

	@PostMapping
	public ResponseEntity<RepairReportResponse> createRepairReport(
		@Valid @RequestBody RepairReportCreateRequest request
	) {
		RepairReportResponse response = repairReportService.createRepairReport(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping
	public ResponseEntity<List<RepairReportResponse>> getRepairReports() {
		List<RepairReportResponse> responses = repairReportService.getRepairReports();

		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{reportId}")
	public ResponseEntity<RepairReportResponse> getRepairReport(
		@PathVariable Long reportId
	) {
		RepairReportResponse response = repairReportService.getRepairReport(reportId);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{reportId}")
	public ResponseEntity<RepairReportResponse> updateRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody RepairReportUpdateRequest request
	) {
		RepairReportResponse response = repairReportService.updateRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{reportId}")
	public ResponseEntity<Void> deleteRepairReport(
		@PathVariable Long reportId
	) {
		repairReportService.deleteRepairReport(reportId);

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{reportId}/submit")
	public ResponseEntity<RepairReportResponse> submitRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody ReportStatusChangeRequest request
	) {
		RepairReportResponse response = repairReportService.submitRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{reportId}/approve")
	public ResponseEntity<RepairReportResponse> approveRepairReport(
		@PathVariable Long reportId,
		@Valid @RequestBody ReportStatusChangeRequest request
	) {
		RepairReportResponse response = repairReportService.approveRepairReport(reportId, request);

		return ResponseEntity.ok(response);
	}
}