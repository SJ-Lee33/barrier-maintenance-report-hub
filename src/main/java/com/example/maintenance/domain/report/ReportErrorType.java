package com.example.maintenance.domain.report;

import com.example.maintenance.domain.errortype.ErrorType;
import com.example.maintenance.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "report_error_types", uniqueConstraints = {
	@UniqueConstraint( // 한 리포트에 같은 오류 유형이 여러 번 들어가는 걸 방지
		name = "uk_report_error_type", columnNames = {"report_id", "error_type_id"}
	)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportErrorType extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 오류 유형이 참조할 유지보수 리포트
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private RepairReport repairReport;

	/**
	 * 그 리포트에 선택된 오류 유형
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "error_type_id", nullable = false)
	private ErrorType errorType;

	public ReportErrorType(RepairReport repairReport, ErrorType errorType) {
		this.repairReport = repairReport;
		this.errorType = errorType;
	}
}