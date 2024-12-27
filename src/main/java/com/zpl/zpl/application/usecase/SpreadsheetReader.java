package com.zpl.zpl.application.usecase;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class SpreadsheetReader {
    private static final List<String> MANDATORY_COLUMNS = Arrays.asList("EAN", "SKU", "Quantidade");

    public List<Map<String, Object>> readSpreadsheet(File file) throws IOException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("The spreadsheet is empty or does not have a header row.");
            }

            List<String> columns = new ArrayList<>();
            for (Cell cell : headerRow) {
                columns.add(cell.getStringCellValue());
            }

            validateMandatoryColumns(columns);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                Map<String, Object> rowData = new HashMap<>();
                for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String columnName = columns.get(colIndex);
                    rowData.put(columnName, getCellValue(cell));
                }
                data.add(rowData);
            }
        }
        return data;
    }

    private void validateMandatoryColumns(List<String> columns) throws IOException {
        for (String mandatoryColumn : MANDATORY_COLUMNS) {
            if (!columns.contains(mandatoryColumn)) {
                throw new IOException("Missing mandatory column: " + mandatoryColumn);
            }
        }
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (int) numericValue) {
                        return (int) numericValue;
                    } else {
                        return numericValue;
                    }
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
}
