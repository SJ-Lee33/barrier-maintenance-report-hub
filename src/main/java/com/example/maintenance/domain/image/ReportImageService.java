package com.example.maintenance.domain.image;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.maintenance.domain.image.dto.ReportImageResponse;
import com.example.maintenance.domain.report.RepairReport;
import com.example.maintenance.domain.report.RepairReportRepository;
import com.example.maintenance.domain.user.User;
import com.example.maintenance.global.error.ForbiddenException;
import com.example.maintenance.global.error.NotFoundException;
import com.example.maintenance.global.storage.LocalFileStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportImageService {

	private final ReportImageRepository reportImageRepository;
	private final RepairReportRepository repairReportRepository;
	private final LocalFileStorage localFileStorage;

	@Transactional
	public List<ReportImageResponse> uploadImages(
		Long reportId,
		List<MultipartFile> files,
		ReportImageType imageType,
		User currentUser
	) {
		RepairReport repairReport = getRepairReport(reportId);

		validateReportOwner(repairReport, currentUser);

		List<ReportImage> reportImages = files.stream()
			.map(file -> {
				String imageUrl = localFileStorage.store(file, reportId);
				return new ReportImage(repairReport, imageUrl, imageType);
			})
			.toList();

		List<ReportImage> savedImages = reportImageRepository.saveAll(reportImages);

		return savedImages.stream()
			.map(ReportImageResponse::from)
			.toList();
	}

	public List<ReportImageResponse> getImages(Long reportId) {
		RepairReport repairReport = getRepairReport(reportId);

		return reportImageRepository.findAllByRepairReportIdOrderByUploadedAtAsc(
				repairReport.getId()
			)
			.stream()
			.map(ReportImageResponse::from)
			.toList();
	}

	@Transactional
	public void deleteImage(
		Long reportId,
		Long imageId,
		User currentUser
	) {
		RepairReport repairReport = getRepairReport(reportId);

		validateReportOwner(repairReport, currentUser);

		ReportImage reportImage = reportImageRepository.findByIdAndRepairReportId(
				imageId,
				reportId
			)
			.orElseThrow(() -> new NotFoundException("이미지를 찾을 수 없습니다."));

		localFileStorage.delete(reportImage.getImageUrl());
		reportImageRepository.delete(reportImage);
	}

	private RepairReport getRepairReport(Long reportId) {
		return repairReportRepository.findByIdAndDeletedFalse(reportId)
			.orElseThrow(() -> new NotFoundException("리포트를 찾을 수 없습니다."));
	}

	private void validateReportOwner(RepairReport repairReport, User currentUser) {
		Long reportOwnerUserId = repairReport.getTechnician().getUser().getId();

		if (!reportOwnerUserId.equals(currentUser.getId())) {
			throw new ForbiddenException("본인 리포트의 이미지만 처리할 수 있습니다.");
		}
	}
}