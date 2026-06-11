package com.example.maintenance.domain.history;

import java.time.LocalDateTime;

import com.example.maintenance.domain.report.RepairReport;
import com.example.maintenance.domain.report.ReportStatus;
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
@Table(name = "report_status_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportStatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 상태가 변경된 리포트
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private RepairReport repairReport;

	/**
	 * 변경 전 상태
	 * 최초 상태 기록처럼 이전 상태가 없을 수 있으므로 nullable = true
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "from_status", length = 30)
	private ReportStatus fromStatus;

	/**
	 * 변경 후 상태
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "to_status", nullable = false, length = 30)
	private ReportStatus toStatus;

	/**
	 * 상태를 변경한 사용자
	 * 기사, 관리자, 관리팀 모두 users 테이블 기준으로 기록
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "changed_by", nullable = false)
	private User changedBy;

	/**
	 * 상태 변경 사유
	 * 예: 관리팀 승인, 사진 누락으로 반려, 재제출
	 */
	@Column(length = 500)
	private String reason;

	/**
	 * 상태 변경 시각
	 */
	@Column(name = "changed_at", nullable = false)
	private LocalDateTime changedAt;

	public ReportStatusHistory(
		RepairReport repairReport,
		ReportStatus fromStatus,
		ReportStatus toStatus,
		User changedBy,
		String reason
	) {
		this.repairReport = repairReport;
		this.fromStatus = fromStatus;
		this.toStatus = toStatus;
		this.changedBy = changedBy;
		this.reason = reason;
	}

	@PrePersist
	public void prePersist() {
		this.changedAt = LocalDateTime.now();
	}
}