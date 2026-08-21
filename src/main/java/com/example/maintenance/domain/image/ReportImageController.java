package com.example.maintenance.domain.image;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.maintenance.domain.image.dto.ReportImageResponse;
import com.example.maintenance.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/repair-reports/{reportId}/images")
@Tag(name = "Report Image API", description = "유지보수 리포트 이미지 업로드, 조회, 삭제 API")
@SecurityRequirement(name = "bearerAuth")
public class ReportImageController {

	private final ReportImageService reportImageService;

	@Operation(summary = "리포트 이미지 업로드", description = "본인 리포트에 수리 전/후/기타 이미지를 다중 업로드합니다.")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<ReportImageResponse>> uploadImages(
		@PathVariable Long reportId,
		@RequestPart("files") @NotEmpty List<MultipartFile> files,
		@RequestParam ReportImageType imageType,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		List<ReportImageResponse> responses = reportImageService.uploadImages(
			reportId,
			files,
			imageType,
			userDetails.getUser()
		);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(responses);
	}

	@Operation(summary = "리포트 이미지 조회", description = "특정 리포트에 첨부된 이미지 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<List<ReportImageResponse>> getImages(
		@PathVariable Long reportId
	) {
		List<ReportImageResponse> responses = reportImageService.getImages(reportId);

		return ResponseEntity.ok(responses);
	}

	@Operation(summary = "리포트 이미지 삭제", description = "본인 리포트에 첨부된 이미지를 삭제합니다.")
	@DeleteMapping("/{imageId}")
	public ResponseEntity<Void> deleteImage(
		@PathVariable Long reportId,
		@PathVariable Long imageId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		reportImageService.deleteImage(
			reportId,
			imageId,
			userDetails.getUser()
		);

		return ResponseEntity.noContent().build();
	}
}