package com.example.maintenance.domain.errortype;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.maintenance.domain.errortype.dto.ErrorTypeResponse;
import com.example.maintenance.global.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ErrorTypeService {

	private final ErrorTypeRepository errorTypeRepository;

	public List<ErrorTypeResponse> getErrorTypes() {
		return errorTypeRepository.findAll()
			.stream()
			.map(ErrorTypeResponse::from)
			.toList();
	}

	public ErrorTypeResponse getErrorType(Long errorTypeId) {
		ErrorType errorType = errorTypeRepository.findById(errorTypeId)
			.orElseThrow(() -> new NotFoundException("오류 유형을 찾을 수 없습니다."));

		return ErrorTypeResponse.from(errorType);
	}
}