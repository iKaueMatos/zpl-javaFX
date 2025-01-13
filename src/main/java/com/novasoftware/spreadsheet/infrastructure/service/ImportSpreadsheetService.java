package com.novasoftware.spreadsheet.infrastructure.service;

import com.novasoftware.Supplier.domain.model.Supplier;
import com.novasoftware.brand.domain.model.Brand;
import com.novasoftware.category.domain.model.Category;
import com.novasoftware.product.application.dto.ProductData;
import com.novasoftware.product.application.repository.ProductRepository;
import com.novasoftware.product.infrastructure.repository.ProductRepositoryImpl;
import com.novasoftware.shared.util.alert.CustomAlert;
import com.novasoftware.spreadsheet.application.repository.ImportHistoryRepository;
import com.novasoftware.spreadsheet.domain.model.ImportHistory;
import com.novasoftware.spreadsheet.infrastructure.repository.ImportHistoryRepositoryImpl;
import com.novasoftware.tools.application.usecase.SpreadsheetReader;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportSpreadsheetService {
    private final SpreadsheetReader spreadsheetReader;
    private final Stage ownerStage;
    private MFXButton downloadErrorButton;
    private List<ProductData> productList = new CopyOnWriteArrayList<>();

    public ImportSpreadsheetService(Stage ownerStage, MFXButton downloadErrorButton) {
        this.spreadsheetReader = new SpreadsheetReader();
        this.ownerStage = ownerStage;
        this.downloadErrorButton = downloadErrorButton;
    }

    public void importSpreadsheet(File file, Consumer<List<ProductData>> callback) {
        CompletableFuture.supplyAsync(() -> processSpreadsheet(file))
                .thenAccept(callback);
    }

    private List<ProductData> processSpreadsheet(File file) {
        List<ProductData> productDataList = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();
        boolean hasError = false;

        try {
            List<Map<String, Object>> importedData = spreadsheetReader.readSpreadsheet(file);
            if (importedData.isEmpty()) {
                Platform.runLater(() -> CustomAlert.showWarningAlert(ownerStage, "Dados Vazios", "A planilha não contém dados válidos."));
                return productDataList;
            }

            for (Map<String, Object> row : importedData) {
                boolean rowHasError = false;

                String sku = String.valueOf(row.getOrDefault("SKU", ""));
                if (sku.isEmpty()) {
                    row.put("SKU", "Valor Padrão");
                    rowHasError = true;
                }

                String quantity = String.valueOf(row.getOrDefault("Quantidade", "0"));
                if (Integer.parseInt(quantity) < 1) {
                    row.put("Quantidade", "valor invalido");
                    rowHasError = true;
                }

                ProductData product = mapRowToProductData(row, rowHasError);
                if (rowHasError) {
                    errorData.add(row);
                    hasError = true;
                } else {
                    productDataList.add(product);
                }
            }

            ImportHistoryRepository importHistoryRepository = new ImportHistoryRepositoryImpl();
            if (hasError) {
                generateErrorSpreadsheetAsync(errorData);
                showWarningAlert(ownerStage, "Importação com erros", "Alguns dados foram corrigidos. Um relatório de erros foi gerado.");
                ImportHistory importHistory = createImportHistory(
                        "Importação com erros",
                        "Alguns dados foram corrigidos. Um relatório de erros foi gerado."
                );
                importHistoryRepository.insert(importHistory);
            } else {
                ImportHistory importHistory = createImportHistory(
                        "Importação feita com sucesso",
                        "Nenhum erro foi encontrado"
                );
                importHistoryRepository.insert(importHistory);
                showInfoAlert(ownerStage, "Importação bem-sucedida", "Planilha importada com sucesso!");
            }

        } catch (IOException e) {
            Platform.runLater(() -> CustomAlert.showErrorAlert(ownerStage, "Erro de Importação", "Ocorreu um erro ao importar a planilha."));
        }

        return productDataList;
    }

    private ProductData mapRowToProductData(Map<String, Object> row, boolean rowHasError) {
        String name = String.valueOf(row.getOrDefault("Nome", ""));
        String sku = String.valueOf(row.getOrDefault("SKU", "Valor Padrão"));
        String variationSku = String.valueOf(row.getOrDefault("SKU_VARIACAO", ""));
        String ean = String.valueOf(row.getOrDefault("EAN", ""));
        int quantity = Integer.parseInt(String.valueOf(row.getOrDefault("Quantidade", "0")));
        Double salePrice = Double.parseDouble(String.valueOf(row.getOrDefault("PREÇO_UNITARIO", 0.0)));
        Date expiryDate = null;
        Category category = new Category();
        category.setName(String.valueOf(row.getOrDefault("CATEGORIA", null)));
        Supplier supplier = new Supplier();
        supplier.setName(String.valueOf(row.getOrDefault("MARCA", null)));
        Double weight = 0.0;
        List<String> images = null;
        Date creationDate = new Date();
        Date lastUpdatedDate = new Date();
        Brand brandId = new Brand();
        brandId.setName(String.valueOf(row.getOrDefault("MARCA", null)));
        brandId.setCreated_at(new Date());
        brandId.setUpdated_at(null);
        brandId.setDescription(String.valueOf(row.getOrDefault("DESCRIÇÃO", null)));

        String description = null;
        String error = rowHasError ? "Erro detectado" : null;

        return new ProductData(
                name, sku, variationSku, ean, quantity, salePrice, category, expiryDate, supplier, weight, images, creationDate, lastUpdatedDate, error, brandId, description
        );
    }

    private void generateErrorSpreadsheetAsync(List<Map<String, Object>> errorData) {
        CompletableFuture.runAsync(() -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Erros");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Id");
                headerRow.createCell(1).setCellValue("Nome");
                headerRow.createCell(2).setCellValue("SKU");
                headerRow.createCell(3).setCellValue("Quantidade");
                headerRow.createCell(4).setCellValue("Erro");

                int rowIndex = 1;
                for (Map<String, Object> row : errorData) {
                    Row excelRow = sheet.createRow(rowIndex++);
                    excelRow.createCell(0).setCellValue(String.valueOf(row.getOrDefault("Id", "")));
                    excelRow.createCell(1).setCellValue(String.valueOf(row.getOrDefault("Nome", "")));
                    excelRow.createCell(2).setCellValue(String.valueOf(row.getOrDefault("SKU", "")));
                    excelRow.createCell(3).setCellValue(String.valueOf(row.getOrDefault("Quantidade", "")));
                    excelRow.createCell(4).setCellValue("Erro encontrado (SKU vazio ou Quantidade inválida)");
                }

                File errorFile = new File("Planilha_Erros_" + System.currentTimeMillis() + ".xlsx");
                errorFile.getParentFile().mkdirs();
                try (FileOutputStream fileOut = new FileOutputStream(errorFile)) {
                    workbook.write(fileOut);
                }

                Platform.runLater(() -> downloadErrorButton.setVisible(true));
                CustomAlert.showInfoAlert(ownerStage, "Sucesso", "Planilha de erros gerada com sucesso!");
            } catch (IOException e) {
                Platform.runLater(() -> CustomAlert.showErrorAlert(ownerStage, "Erro", "Erro ao gerar planilha de erros."));
            }
        });
    }

    private void showWarningAlert(Stage ownerStage, String title, String message) {
        Platform.runLater(() -> CustomAlert.showWarningAlert(ownerStage, title, message));
    }

    private void showInfoAlert(Stage ownerStage, String title, String message) {
        Platform.runLater(() -> CustomAlert.showInfoAlert(ownerStage, title, message));
    }

    private ImportHistory createImportHistory(String status, String errorDetails) {
        ImportHistory importHistory = new ImportHistory();
        Date now = new Date();

        importHistory.setImportDate(now);
        importHistory.setCreatedAt(now);
        importHistory.setUpdatedAt(now);
        importHistory.setStatus(status);
        importHistory.setErrorDetails(errorDetails);

        return importHistory;
    }

}
