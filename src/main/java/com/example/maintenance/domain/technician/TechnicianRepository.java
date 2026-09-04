package com.example.maintenance.domain.technician;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {

	boolean existsByUserId(Long userId);

	boolean existsByEmpNo(String empNo);

	Optional<Technician> findByUserId(Long userId);
}