package com.example.maintenance.domain.errortype;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorTypeRepository extends JpaRepository<ErrorType, Long> {

	boolean existsByCode(String code);

	Optional<ErrorType> findByCode(String code);
}