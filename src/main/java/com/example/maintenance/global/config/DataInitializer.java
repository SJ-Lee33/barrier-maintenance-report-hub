package com.example.maintenance.global.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.errortype.ErrorType;
import com.example.maintenance.domain.errortype.ErrorTypeCode;
import com.example.maintenance.domain.errortype.ErrorTypeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final ErrorTypeRepository errorTypeRepository;

	@Override
	@Transactional
	public void run(String... args) {
		for (ErrorTypeCode errorTypeCode : ErrorTypeCode.values()) {
			if (!errorTypeRepository.existsByCode(errorTypeCode.getCode())) {
				errorTypeRepository.save(ErrorType.from(errorTypeCode));

				// enum을 그대로 DB에 넣는 게 아니라, enum을 ErrorType 엔티티로 변환한 뒤 저장하는 부분
			}
		}
	}
}