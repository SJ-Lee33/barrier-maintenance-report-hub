package com.example.maintenance.domain.export;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.example.maintenance.domain.export.dto.ExternalReportExportResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JsonExportFileGenerator {

	private final ObjectMapper objectMapper;

	public void generate(
		ExternalReportExportResponse exportResponse,
		Path filePath
	) {
		try {
			objectMapper
				.writerWithDefaultPrettyPrinter()
				.writeValue(filePath.toFile(), exportResponse);
		} catch (IOException exception) {
			throw new IllegalStateException("JSON Export 파일 생성에 실패했습니다.");
		}
	}
}