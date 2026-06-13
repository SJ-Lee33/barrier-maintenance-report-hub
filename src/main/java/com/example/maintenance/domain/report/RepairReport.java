package com.example.maintenance.domain.report;

import java.time.LocalDateTime;

import com.example.maintenance.domain.device.Device;
import com.example.maintenance.domain.technician.Technician;
import com.example.maintenance.global.common.BaseTimeEntity;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "repair_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepairReport extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 리포트를 작성한 수리기사
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "technician_id", nullable = false)
	private Technician technician;

	/**
	 * 유지보수 대상 장비
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	/**
	 * 리포트 제목
	 */
	@Column(nullable = false, length = 100)
	private String title;

	/**
	 * 현장 상황 및 오류 설명
	 */
	@Column(nullable = false, length = 2000)
	private String description;

	/**
	 * 실제 수리 조치 내용
	 */
	@Column(name = "repair_action", length = 2000)
	private String repairAction;

	/**
	 * 리포트 상태
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ReportStatus status;

	/**
	 * 리포트 삭제 (soft delete) - false인 것만 조회됨
	 */
	@Column(nullable = false)
	private boolean deleted = false;

	/**
	 * 오류 발생 시각
	 */
	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;

	/**
	 * 수리 완료 시각
	 */
	@Column(name = "repaired_at")
	private LocalDateTime repairedAt;

	public RepairReport(
		Technician technician,
		Device device,
		String title,
		String description,
		String repairAction,
		LocalDateTime occurredAt,
		LocalDateTime repairedAt
	) {
		this.technician = technician;
		this.device = device;
		this.title = title;
		this.description = description;
		this.repairAction = repairAction;
		this.status = ReportStatus.DRAFT; // 초기값 설정
		this.occurredAt = occurredAt;
		this.repairedAt = repairedAt;
	}

	// 수정 매서드
	public void update(
		String title,
		String description,
		String repairAction,
		LocalDateTime occurredAt,
		LocalDateTime repairedAt
	) {
		if (title != null) {
			this.title = title;
		}

		if (description != null) {
			this.description = description;
		}

		if (repairAction != null) {
			this.repairAction = repairAction;
		}

		if (occurredAt != null) {
			this.occurredAt = occurredAt;
		}

		if (repairedAt != null) {
			this.repairedAt = repairedAt;
		}
	}

	// 삭제 매서드
	public void delete() {
		this.deleted = true;
	}
}