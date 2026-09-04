package com.example.maintenance.domain.export;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.example.maintenance.domain.export.dto.ExternalReportExportResponse;

@Component
public class CsvExportFileGenerator {

	public void generate(
		ExternalReportExportResponse exportResponse,
		Path filePath
	) {
		String csvContent = createCsvContent(exportResponse);

		try {
			Files.writeString(
				filePath,
				csvContent,
				StandardCharsets.UTF_8
			);
		} catch (IOException exception) {
			throw new IllegalStateException("CSV Export 파일 생성에 실패했습니다.");
		}
	}

	private String createCsvContent(ExternalReportExportResponse exportResponse) {
		return String.join(",",
			"작업자",
			"장비기번",
			"장비위치",
			"고장유형",
			"고장내용",
			"조치내용",
			"고장발생시각",
			"수리완료시각"
		) + "\n" +
			String.join(",",
				escape(exportResponse.technicianName()),
				escape(exportResponse.deviceSerialNo()),
				escape(exportResponse.deviceLocation()),
				escape(exportResponse.errorTypeNames()),
				escape(exportResponse.description()),
				escape(exportResponse.repairAction()),
				escape(exportResponse.occurredAt()),
				escape(exportResponse.repairedAt())
			);
	}

	private String escape(String value) {
		if (value == null) {
			return "";
		}

		String escapedValue = value.replace("\"", "\"\"");

		if (
			escapedValue.contains(",") ||
				escapedValue.contains("\"") ||
				escapedValue.contains("\n") ||
				escapedValue.contains("\r")
		) {
			return "\"" + escapedValue + "\"";
		}

		return escapedValue;
	}
}