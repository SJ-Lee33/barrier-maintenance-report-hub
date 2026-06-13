package com.example.maintenance.domain.technician.dto;

import com.example.maintenance.domain.technician.Technician;

public record TechnicianResponse(
	Long id,
	Long userId,
	String userName,
	String userEmail,
	String phone,
	String department,
	String empNo
) {

	public static TechnicianResponse from(Technician technician) {
		return new TechnicianResponse(
			technician.getId(),
			technician.getUser().getId(),
			technician.getUser().getName(),
			technician.getUser().getEmail(),
			technician.getPhone(),
			technician.getDepartment(),
			technician.getEmpNo()
		);
	}
}