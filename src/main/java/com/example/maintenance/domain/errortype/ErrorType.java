package com.example.maintenance.domain.errortype;

import com.example.maintenance.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "error_types")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorType extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 오류 유형 코드
	 * 예: SENSOR_ERROR, POWER_FAILURE, ARM_STUCK
	 * 나중에 새로운 오류 코드가 추가되어도 DB/API 기반으로 확장할 수 있도록 enum이 아니라 스트링으로 저장
	 */
	@Column(nullable = false, unique = true, length = 50)
	private String code;

	/**
	 * 화면에 표시할 오류 유형 이름
	 * 예: 센서 오류, 전원 장애, 차단봉 걸림
	 */
	@Column(nullable = false, length = 100)
	private String name;

	/**
	 * 오류 유형에 대한 설명
	 */
	@Column(length = 500)
	private String description;

	public ErrorType(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	public static ErrorType from(ErrorTypeCode errorTypeCode) {
		return new ErrorType(
			errorTypeCode.getCode(),
			errorTypeCode.getDisplayName(),
			errorTypeCode.getDescription()
		);
	}
}