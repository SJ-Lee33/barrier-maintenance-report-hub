package com.example.maintenance.domain.technician;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.technician.dto.TechnicianCreateRequest;
import com.example.maintenance.domain.technician.dto.TechnicianResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Technician API", description = "현장 기사 프로필 등록 및 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/technicians")
public class TechnicianController {

	private final TechnicianService technicianService;

	@PostMapping
	public ResponseEntity<TechnicianResponse> createTechnician(
		@Valid @RequestBody TechnicianCreateRequest request
	) {
		TechnicianResponse response = technicianService.createTechnician(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping
	public ResponseEntity<List<TechnicianResponse>> getTechnicians() {
		List<TechnicianResponse> responses = technicianService.getTechnicians();

		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{technicianId}")
	public ResponseEntity<TechnicianResponse> getTechnician(
		@PathVariable Long technicianId
	) {
		TechnicianResponse response = technicianService.getTechnician(technicianId);

		return ResponseEntity.ok(response);
	}
}