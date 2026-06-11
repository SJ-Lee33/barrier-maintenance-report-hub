package com.example.maintenance.domain.device;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {

	// 장비 시리얼번호 중복 방지를 위해 확인용 함수
	boolean existsBySerialNo(String serialNo);

	Optional<Device> findBySerialNo(String serialNo);
}

/*
 * JpaRepository<Device, Long>
 * 이걸로 기본적인 DB 기능이 자동 생성됐음
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 * */