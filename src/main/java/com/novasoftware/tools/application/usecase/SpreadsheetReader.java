package com.novasoftware.tools.application.usecase;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadsheetReader {
    private static final List<String> MANDATORY_COLUMNS = Arrays.asList("Nome", "SKU_VARIACAO", "EAN", "SKU", "Quantidade");

    public List<Map<String, Object>> readSpreadsheet(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("The file is null or does not exist.");
        }

        List<Map<String, Object>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IOException("The spreadsheet does not contain any sheets.");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("The spreadsheet is empty or does not have a header row.");
            }

            List<String> columns = extractColumnHeaders(headerRow);
            validateMandatoryColumns(columns);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row)) continue;

                Map<String, Object> rowData = extractRowData(columns, row);
                data.add(rowData);
            }
        } catch (IOException e) {
            throw new IOException("Error reading spreadsheet: " + e.getMessage(), e);
        }
        return data;
    }

    private List<String> extractColumnHeaders(Row headerRow) {
        List<String> columns = new ArrayList<>();
        for (Cell cell : headerRow) {
            columns.add(cell.getStringCellValue().trim());
        }
        return columns;
    }

    private void validateMandatoryColumns(List<String> columns) throws IOException {
        for (String mandatoryColumn : MANDATORY_COLUMNS) {
            if (!columns.contains(mandatoryColumn)) {
                throw new IOException("Missing mandatory column: " + mandatoryColumn);
            }
        }
    }

    private Map<String, Object> extractRowData(List<String> columns, Row row) {
        Map<String, Object> rowData = new HashMap<>();
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            String columnName = columns.get(colIndex);
            Cell cell = row.getCell(colIndex);
            rowData.put(columnName, getCellValue(cell));
        }
        return rowData;
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
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
                return evaluateFormulaCell(cell);
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

    private Object evaluateFormulaCell(Cell cell) {
        try {
            return cell.getStringCellValue();
        } catch (IllegalStateException e) {
            return cell.getNumericCellValue();
        }
    }
}
