package com.example.maintenance.global.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.maintenance.domain.export.ExportType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExportFileStorage {

	private final FileStorageProperties fileStorageProperties;

	public ExportFilePath createExportFilePath(Long reportId, ExportType exportType) {
		String extension = getExtension(exportType);
		String storedFilename = UUID.randomUUID() + "." + extension;

		Path reportDirectory = Path.of(
				fileStorageProperties.exportDir(),
				String.valueOf(reportId)
			)
			.toAbsolutePath()
			.normalize();

		try {
			Files.createDirectories(reportDirectory);
		} catch (IOException exception) {
			throw new IllegalStateException("Export 파일 저장 폴더 생성에 실패했습니다.");
		}

		Path filePath = reportDirectory.resolve(storedFilename);

		String fileUrl = "/exports/reports/" + reportId + "/" + storedFilename;

		return new ExportFilePath(filePath, fileUrl);
	}

	private String getExtension(ExportType exportType) {
		return switch (exportType) {
			case JSON -> "json";
			case CSV -> "csv";
			case EXCEL -> "xlsx";
		};
	}

	public record ExportFilePath(
		Path filePath,
		String fileUrl
	) {
	}
}