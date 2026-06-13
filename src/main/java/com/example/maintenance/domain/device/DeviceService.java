package com.example.maintenance.domain.device;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.device.dto.DeviceCreateRequest;
import com.example.maintenance.domain.device.dto.DeviceResponse;
import com.example.maintenance.domain.device.dto.DeviceUpdateRequest;
import com.example.maintenance.global.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService {

	private final DeviceRepository deviceRepository;

	@Transactional
	public DeviceResponse createDevice(DeviceCreateRequest request) {
		if (deviceRepository.existsBySerialNo(request.serialNo())) {
			throw new IllegalArgumentException("이미 등록된 장비 시리얼 번호입니다.");
		}

		Device device = new Device(
			request.serialNo(),
			request.location(),
			request.modelName(),
			request.installedAt()
		);

		Device savedDevice = deviceRepository.save(device);

		return DeviceResponse.from(savedDevice);
	}

	public List<DeviceResponse> getDevices() {
		return deviceRepository.findAll()
			.stream()
			.map(DeviceResponse::from)
			.toList();
	}

	public DeviceResponse getDevice(Long deviceId) {
		Device device = deviceRepository.findById(deviceId)
			.orElseThrow(() -> new NotFoundException("장비를 찾을 수 없습니다."));

		return DeviceResponse.from(device);
	}

	@Transactional
	public DeviceResponse updateDevice(Long deviceId, DeviceUpdateRequest request) {
		Device device = deviceRepository.findById(deviceId)
			.orElseThrow(() -> new NotFoundException("장비를 찾을 수 없습니다."));

		device.update(
			request.location(),
			request.modelName(),
			request.installedAt()
		);

		return DeviceResponse.from(device);
	}

	@Transactional
	public void deleteDevice(Long deviceId) {
		Device device = deviceRepository.findById(deviceId)
			.orElseThrow(() -> new NotFoundException("장비를 찾을 수 없습니다."));

		deviceRepository.delete(device);
	}
}