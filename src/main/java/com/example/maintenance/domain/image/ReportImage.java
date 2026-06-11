package com.example.maintenance.domain.image;

import java.time.LocalDateTime;

import com.example.maintenance.domain.report.RepairReport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "report_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 이미지가 첨부된 유지보수 리포트
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private RepairReport repairReport;

	/**
	 * 이미지 접근 URL
	 * 초기에는 로컬 파일 경로 또는 로컬 URL을 저장하고,
	 * 이후 확장 시 외부 스토리지 URL을 저장
	 */
	@Column(name = "image_url", nullable = false, length = 500)
	private String imageUrl;

	/**
	 * 수리 전/후/기타 이미지 구분
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "image_type", nullable = false, length = 20)
	private ReportImageType imageType;

	/**
	 * 이미지 업로드 시각
	 */
	@Column(name = "uploaded_at", nullable = false)
	private LocalDateTime uploadedAt;

	public ReportImage(RepairReport repairReport, String imageUrl, ReportImageType imageType) {
		this.repairReport = repairReport;
		this.imageUrl = imageUrl;
		this.imageType = imageType;
	}

	@PrePersist
	public void prePersist() {
		this.uploadedAt = LocalDateTime.now();
	}
}