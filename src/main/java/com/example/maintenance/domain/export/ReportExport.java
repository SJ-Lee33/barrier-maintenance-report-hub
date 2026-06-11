package com.example.maintenance.domain.export;

import java.time.LocalDateTime;

import com.example.maintenance.domain.report.RepairReport;
import com.example.maintenance.domain.user.User;

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
@Table(name = "report_exports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportExport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Export 대상 리포트
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private RepairReport repairReport;

	/**
	 * Export 형식
	 * 예: EXCEL, CSV, JSON
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "export_type", nullable = false, length = 20)
	private ExportType exportType;

	/**
	 * Export 실행자
	 * 관리팀 또는 관리자 계정
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exported_by", nullable = false)
	private User exportedBy;

	/**
	 * Export 실행 시각
	 */
	@Column(name = "exported_at", nullable = false)
	private LocalDateTime exportedAt;

	/**
	 * 생성된 파일 URL 또는 저장 경로
	 */
	@Column(name = "file_url", nullable = false, length = 500)
	private String fileUrl;

	public ReportExport(
		RepairReport repairReport,
		ExportType exportType,
		User exportedBy,
		String fileUrl
	) {
		this.repairReport = repairReport;
		this.exportType = exportType;
		this.exportedBy = exportedBy;
		this.fileUrl = fileUrl;
	}

	@PrePersist
	public void prePersist() {
		this.exportedAt = LocalDateTime.now();
	}
}