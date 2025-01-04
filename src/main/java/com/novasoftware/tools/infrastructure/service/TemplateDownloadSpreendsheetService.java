package com.novasoftware.tools.infrastructure.service;

import com.novasoftware.shared.util.alert.CustomAlert;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TemplateDownloadSpreendsheetService {
    @FXML
    public void downloadTemplate(Stage ownerStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("template-nova-tools.xlsx");
        File file = fileChooser.showSaveDialog(ownerStage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Template");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Nome");
                headerRow.createCell(1).setCellValue("EAN");
                headerRow.createCell(2).setCellValue("SKU");
                headerRow.createCell(3).setCellValue("SKU_VARIACAO");
                headerRow.createCell(4).setCellValue("Quantidade");

                Row row1 = sheet.createRow(1);
                row1.createCell(0).setCellValue("Produto 1");
                row1.createCell(1).setCellValue("1234567890123");
                row1.createCell(2).setCellValue("SKU123");
                row1.createCell(3).setCellValue("VARIACAO1");
                row1.createCell(4).setCellValue(10);

                Row row2 = sheet.createRow(2);
                row2.createCell(0).setCellValue("Produto 2");
                row2.createCell(1).setCellValue("9876543210987");
                row2.createCell(2).setCellValue("SKU987");
                row2.createCell(3).setCellValue("VARIACAO2");
                row2.createCell(4).setCellValue(20);

                for (int i = 0; i < 5; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                CustomAlert.showInfoAlert(ownerStage, "Salvar Modelo de Planilha", "Modelo de planilha salvo com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
                CustomAlert.showErrorAlert(ownerStage, "Erro ao Salvar", "Ocorreu um erro ao salvar o modelo de planilha.");
            }
        }
    }
}
