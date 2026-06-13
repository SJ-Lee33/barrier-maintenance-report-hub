package com.example.maintenance.domain.report;

public enum ReportStatus {
	DRAFT,
	SUBMITTED,
	REVIEWING,
	APPROVED,
	EXPORTED,
	REJECTED,
	RESUBMITTED
}

/*
 * DRAFT: 임시저장
 * SUBMITTED: 제출됨
 * REVIEWING: 검토 중
 * APPROVED: 승인됨
 * EXPORTED: 외부 보고 양식으로 변환됨
 * REJECTED: 반려됨
 * RESUBMITTED: 재제출됨
 * */