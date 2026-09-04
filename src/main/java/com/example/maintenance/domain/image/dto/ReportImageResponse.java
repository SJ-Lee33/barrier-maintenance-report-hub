package com.example.maintenance.domain.image.dto;

import java.time.LocalDateTime;

import com.example.maintenance.domain.image.ReportImage;
import com.example.maintenance.domain.image.ReportImageType;

public record ReportImageResponse(
	Long id,
	Long reportId,
	String imageUrl,
	ReportImageType imageType,
	LocalDateTime uploadedAt
) {

	public static ReportImageResponse from(ReportImage reportImage) {
		return new ReportImageResponse(
			reportImage.getId(),
			reportImage.getRepairReport().getId(),
			reportImage.getImageUrl(),
			reportImage.getImageType(),
			reportImage.getUploadedAt()
		);
	}
}