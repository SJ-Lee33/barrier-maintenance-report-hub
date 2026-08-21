package com.example.maintenance.global.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LocalFileStorage {

	private final FileStorageProperties fileStorageProperties;

	public String store(MultipartFile file, Long reportId) {
		validateFile(file);

		String originalFilename = file.getOriginalFilename();
		String extension = extractExtension(originalFilename);
		String storedFilename = UUID.randomUUID() + "." + extension;

		Path reportDirectory = Path.of(
			fileStorageProperties.uploadDir(),
			String.valueOf(reportId)
		);

		try {
			Files.createDirectories(reportDirectory);

			Path targetPath = reportDirectory.resolve(storedFilename);
			file.transferTo(targetPath);

			return "/" + fileStorageProperties.uploadDir() + "/" + reportId + "/" + storedFilename;
		} catch (IOException exception) {
			throw new IllegalStateException("파일 저장에 실패했습니다.");
		}
	}

	public void delete(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) {
			return;
		}

		String normalizedPath = imageUrl.startsWith("/")
			? imageUrl.substring(1)
			: imageUrl;

		try {
			Files.deleteIfExists(Path.of(normalizedPath));
		} catch (IOException exception) {
			throw new IllegalStateException("파일 삭제에 실패했습니다.");
		}
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("업로드할 파일이 비어 있습니다.");
		}

		if (file.getSize() > fileStorageProperties.maxImageSize()) {
			throw new IllegalArgumentException("이미지 파일은 5MB 이하만 업로드할 수 있습니다.");
		}

		String contentType = file.getContentType();

		if (
			contentType == null ||
				!(
					contentType.equals("image/jpeg") ||
						contentType.equals("image/png") ||
						contentType.equals("image/webp")
				)
		) {
			throw new IllegalArgumentException("jpg, png, webp 이미지 파일만 업로드할 수 있습니다.");
		}

		String extension = extractExtension(file.getOriginalFilename());

		if (
			!(
				extension.equals("jpg") ||
					extension.equals("jpeg") ||
					extension.equals("png") ||
					extension.equals("webp")
			)
		) {
			throw new IllegalArgumentException("jpg, jpeg, png, webp 확장자만 업로드할 수 있습니다.");
		}
	}

	private String extractExtension(String filename) {
		if (filename == null || filename.isBlank() || !filename.contains(".")) {
			throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다.");
		}

		return filename
			.substring(filename.lastIndexOf(".") + 1)
			.toLowerCase();
	}
}