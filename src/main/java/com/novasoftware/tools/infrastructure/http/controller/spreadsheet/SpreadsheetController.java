package com.novasoftware.tools.infrastructure.http.controller.spreadsheet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.novasoftware.shared.util.alert.CustomAlert;
import com.novasoftware.shared.util.log.DiscordLogger;
import com.novasoftware.tools.application.usecase.SpreadsheetReader;
import com.novasoftware.tools.infrastructure.service.TemplateDownloadSpreendsheetService;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static com.novasoftware.shared.util.log.DiscordLogger.COLOR_RED;

public class SpreadsheetController implements Initializable {
    @FXML
    private MFXTableView<Map<String, Object>> table;
    @FXML
    private MFXTableView<Map<String, Object>> historyTable;
    @FXML
    private MFXTableView<Map<String, Object>> errorLogTable;

    private final SpreadsheetReader spreadsheetReader;
    private final ObservableList<Map<String, Object>> data;
    private final ObservableList<Map<String, Object>> historyData;
    private final ObservableList<Map<String, Object>> errorLogData;

    private Stage ownerStage;

    public SpreadsheetController() {
        this.spreadsheetReader = new SpreadsheetReader();
        this.data = FXCollections.observableArrayList();
        this.historyData = FXCollections.observableArrayList();
        this.errorLogData = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilter();
        setupHistoryTable();
    }

    private void setupTable() {
        MFXTableColumn<Map<String, Object>> id = new MFXTableColumn<>("Id", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("Id", ""))));
        MFXTableColumn<Map<String, Object>> name = new MFXTableColumn<>("Nome", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("Nome", ""))));
        MFXTableColumn<Map<String, Object>> skuColumn = new MFXTableColumn<>("SKU", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("SKU", ""))));
        MFXTableColumn<Map<String, Object>> skuVariation = new MFXTableColumn<>("SKU_VARIACAO", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("SKU_VARIACAO", ""))));
        MFXTableColumn<Map<String, Object>> eanColumn = new MFXTableColumn<>("GTIN", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("GTIN", ""))));
        MFXTableColumn<Map<String, Object>> quantityColumn = new MFXTableColumn<>("Quantidade", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("Quantidade", ""))));

        id.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("Id", "0"))));
        name.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("Nome", "N/A"))));
        eanColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("EAN", "N/A"))));
        skuColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("SKU", "N/A"))));
        skuVariation.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("SKU_VARIACAO", "N/A"))));
        quantityColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("Quantidade", "0"))));

        table.getTableColumns().addAll(id, name, eanColumn, skuColumn, skuVariation, quantityColumn);
    }

    private void setupFilter() {
        table.getFilters().addAll(
                new IntegerFilter<>("Id", map -> Integer.valueOf(String.valueOf(map.getOrDefault("Id", "0")))),
                new StringFilter<>("Nome", map -> String.valueOf(map.getOrDefault("Nome", ""))),
                new StringFilter<>("Sku", map -> String.valueOf(map.getOrDefault("SKU", ""))),
                new StringFilter<>("Sku variação", map -> String.valueOf(map.getOrDefault("SKU_VARIACAO", ""))),
                new StringFilter<>("GTIN", map -> String.valueOf(map.getOrDefault("EAN", ""))),
                new StringFilter<>("Quantidade", map -> String.valueOf(map.getOrDefault("Quantidade", "0")))
        );
    }

    private void setupHistoryTable() {
        MFXTableColumn<Map<String, Object>> historyDate = new MFXTableColumn<>("Data", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("Data", ""))));
        MFXTableColumn<Map<String, Object>> historyStatus = new MFXTableColumn<>("Status", true,
                Comparator.comparing(map -> String.valueOf(map.getOrDefault("Status", ""))));

        historyDate.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("Data", "N/A"))));
        historyStatus.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("Status", "N/A"))));

        historyTable.getTableColumns().addAll(historyDate, historyStatus);
    }

    @FXML
    public void importSpreadsheet() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(ownerStage);

        if (file != null) {
            try {
                List<Map<String, Object>> importedData = spreadsheetReader.readSpreadsheet(file);
                if (!importedData.isEmpty()) {
                    data.addAll(importedData);
                    int idCounter = 1;  // Start the ID counter at 1

                    for (Map<String, Object> row : importedData) {
                        row.put("Id", idCounter);
                        idCounter++;
                    }

                    table.setItems(data);
                    table.setCache(true);

                    ZonedDateTime nowInBrazil = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String formattedDate = nowInBrazil.format(formatter);

                    historyData.add(Map.of("Data", formattedDate, "Status", "Importação bem-sucedida"));
                    historyTable.setItems(historyData);
                    CustomAlert.showInfoAlert(ownerStage, "Importação bem-sucedida", "Planilha importada com sucesso!");
                } else {
                    CustomAlert.showWarningAlert(ownerStage, "Dados Vazios", "A planilha não contém dados válidos.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                DiscordLogger.sendLogToDiscord("Erro", "Ocorreu um erro critico ao processar a requisição", e.toString(), SpreadsheetController.class, COLOR_RED);
                CustomAlert.showErrorAlert(ownerStage, "Erro de Importação", "Ocorreu um erro ao importar a planilha.");
            }
        }
    }

    @FXML
    public void downloadTemplate() {
        if (ownerStage == null) {
            CustomAlert.showErrorAlert(ownerStage, "Erro de Configuração", "OwnerStage não foi configurado.");
            return;
        }

        TemplateDownloadSpreendsheetService templateService = new TemplateDownloadSpreendsheetService();
        templateService.downloadTemplate(ownerStage);
    }
}