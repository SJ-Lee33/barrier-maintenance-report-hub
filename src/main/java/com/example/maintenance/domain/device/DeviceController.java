package com.example.maintenance.domain.device;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.maintenance.domain.device.dto.DeviceCreateRequest;
import com.example.maintenance.domain.device.dto.DeviceResponse;
import com.example.maintenance.domain.device.dto.DeviceUpdateRequest;

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

	@PatchMapping("/{deviceId}")
	public ResponseEntity<DeviceResponse> updateDevice(
		@PathVariable Long deviceId,
		@Valid @RequestBody DeviceUpdateRequest request
	) {
		DeviceResponse response = deviceService.updateDevice(deviceId, request);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{deviceId}")
	public ResponseEntity<Void> deleteDevice(
		@PathVariable Long deviceId
	) {
		deviceService.deleteDevice(deviceId);

		return ResponseEntity.noContent().build();
	}
}