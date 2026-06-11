package com.example.maintenance.domain.device;

import java.time.LocalDateTime;

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
@Table(name = "devices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 장비 고유 시리얼 번호
	 * 예: GATE-2024-001
	 */
	@Column(name = "serial_no", nullable = false, unique = true, length = 50)
	private String serialNo;

	/**
	 * 장비 설치 위치
	 * 예: 서울 강남구 A주차장 입구
	 */
	@Column(nullable = false, length = 255)
	private String location;

	/**
	 * 장비 모델명
	 * 예: BARRIER-X100
	 */
	@Column(name = "model_name", nullable = false, length = 100)
	private String modelName;

	/**
	 * 장비 설치일
	 * nullable (모를 수도 있음..)
	 */
	@Column(name = "installed_at")
	private LocalDateTime installedAt;

	public Device(String serialNo, String location, String modelName, LocalDateTime installedAt) {
		this.serialNo = serialNo;
		this.location = location;
		this.modelName = modelName;
		this.installedAt = installedAt;
	}

	public void update(String location, String modelName, LocalDateTime installedAt) {
		if (location != null) {
			this.location = location;
		}

		if (modelName != null) {
			this.modelName = modelName;
		}

		if (installedAt != null) {
			this.installedAt = installedAt;
		}
	}
}