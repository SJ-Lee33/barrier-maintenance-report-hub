package com.example.maintenance.domain.device;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.device.dto.DeviceCreateRequest;
import com.example.maintenance.domain.device.dto.DeviceResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/devices")
public class DeviceController {

	private final DeviceService deviceService;

	@PostMapping
	public ResponseEntity<DeviceResponse> createDevice(
		@Valid @RequestBody DeviceCreateRequest request
	) {
		DeviceResponse response = deviceService.createDevice(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping
	public ResponseEntity<List<DeviceResponse>> getDevices() {
		List<DeviceResponse> responses = deviceService.getDevices();
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{deviceId}")
	public ResponseEntity<DeviceResponse> getDevice(
		@PathVariable Long deviceId
	) {
		DeviceResponse response = deviceService.getDevice(deviceId);
		return ResponseEntity.ok(response);
	}
}