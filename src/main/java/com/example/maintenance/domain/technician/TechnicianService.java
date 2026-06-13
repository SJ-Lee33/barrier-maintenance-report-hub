package com.example.maintenance.domain.technician;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.technician.dto.TechnicianCreateRequest;
import com.example.maintenance.domain.technician.dto.TechnicianResponse;
import com.example.maintenance.domain.user.User;
import com.example.maintenance.domain.user.UserRepository;
import com.example.maintenance.domain.user.UserRole;
import com.example.maintenance.global.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechnicianService {

	private final TechnicianRepository technicianRepository;
	private final UserRepository userRepository;

	@Transactional
	public TechnicianResponse createTechnician(TechnicianCreateRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		if (user.getRole() != UserRole.TECHNICIAN) {
			throw new IllegalArgumentException("계정에 TECHNICIAN 권한이 없습니다.");
		}

		if (technicianRepository.existsByUserId(request.userId())) {
			throw new IllegalArgumentException("이미 프로필이 등록되었습니다.");
		}

		if (technicianRepository.existsByEmpNo(request.empNo())) {
			throw new IllegalArgumentException("이미 등록된 사번입니다.");
		}

		Technician technician = new Technician(
			user,
			request.phone(),
			request.department(),
			request.empNo()
		);

		Technician savedTechnician = technicianRepository.save(technician);

		return TechnicianResponse.from(savedTechnician);
	}

	public List<TechnicianResponse> getTechnicians() {
		return technicianRepository.findAll()
			.stream()
			.map(TechnicianResponse::from)
			.toList();
	}

	public TechnicianResponse getTechnician(Long technicianId) {
		Technician technician = technicianRepository.findById(technicianId)
			.orElseThrow(() -> new NotFoundException("기사를 찾을 수 없습니다."));

		return TechnicianResponse.from(technician);
	}
}