package com.example.maintenance.domain.errortype;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.errortype.dto.ErrorTypeResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/error-types")
public class ErrorTypeController {

	private final ErrorTypeService errorTypeService;

	@GetMapping
	public ResponseEntity<List<ErrorTypeResponse>> getErrorTypes() {
		List<ErrorTypeResponse> responses = errorTypeService.getErrorTypes();

		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{errorTypeId}")
	public ResponseEntity<ErrorTypeResponse> getErrorType(
		@PathVariable Long errorTypeId
	) {
		ErrorTypeResponse response = errorTypeService.getErrorType(errorTypeId);

		return ResponseEntity.ok(response);
	}
}