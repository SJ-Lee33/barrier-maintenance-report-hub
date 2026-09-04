package com.example.maintenance.domain.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.example.maintenance.domain.export.dto.ExternalReportExportResponse;

@Component
public class ExcelExportFileGenerator {

	public void generate(
		ExternalReportExportResponse exportResponse,
		Path filePath
	) {
		try (
			Workbook workbook = new XSSFWorkbook();
			OutputStream outputStream = Files.newOutputStream(filePath)
		) {
			Sheet sheet = workbook.createSheet("유지보수 리포트");

			CellStyle headerStyle = createHeaderStyle(workbook);

			Row headerRow = sheet.createRow(0);
			createHeaderCell(headerRow, 0, "작업자", headerStyle);
			createHeaderCell(headerRow, 1, "장비기번", headerStyle);
			createHeaderCell(headerRow, 2, "장비위치", headerStyle);
			createHeaderCell(headerRow, 3, "고장유형", headerStyle);
			createHeaderCell(headerRow, 4, "고장내용", headerStyle);
			createHeaderCell(headerRow, 5, "조치내용", headerStyle);
			createHeaderCell(headerRow, 6, "고장발생시각", headerStyle);
			createHeaderCell(headerRow, 7, "수리완료시각", headerStyle);

			Row dataRow = sheet.createRow(1);
			createCell(dataRow, 0, exportResponse.technicianName());
			createCell(dataRow, 1, exportResponse.deviceSerialNo());
			createCell(dataRow, 2, exportResponse.deviceLocation());
			createCell(dataRow, 3, exportResponse.errorTypeNames());
			createCell(dataRow, 4, exportResponse.description());
			createCell(dataRow, 5, exportResponse.repairAction());
			createCell(dataRow, 6, exportResponse.occurredAt());
			createCell(dataRow, 7, exportResponse.repairedAt());

			for (int i = 0; i < 8; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(outputStream);
		} catch (IOException exception) {
			throw new IllegalStateException("Excel Export 파일 생성에 실패했습니다.");
		}
	}

	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();

		Font font = workbook.createFont();
		font.setBold(true);

		style.setFont(font);

		return style;
	}

	private void createHeaderCell(
		Row row,
		int columnIndex,
		String value,
		CellStyle style
	) {
		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void createCell(
		Row row,
		int columnIndex,
		String value
	) {
		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(value == null ? "" : value);
	}
}