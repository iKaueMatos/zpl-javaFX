package com.novasoftware.tools.infrastructure.http.controller.spreadsheet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.novasoftware.tools.application.usecase.SpreadsheetReader;
import com.novasoftware.tools.ui.util.CustomAlert;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SpreadsheetController implements Initializable {
    @FXML
    private MFXTableView<Map<String, Object>> dataTable;

    @FXML
    private TextArea outputArea;

    private final SpreadsheetReader spreadsheetReader;
    private final ObservableList<Map<String, Object>> data;

    private Stage ownerStage;

    public SpreadsheetController() {
        this.spreadsheetReader = new SpreadsheetReader();
        this.data = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialize as colunas da tabela
        MFXTableColumn<Map<String, Object>> eanColumn = new MFXTableColumn<>("EAN", true, Comparator.comparing(map -> map.get("EAN").toString()));
        MFXTableColumn<Map<String, Object>> skuColumn = new MFXTableColumn<>("SKU", true, Comparator.comparing(map -> map.get("SKU").toString()));
        MFXTableColumn<Map<String, Object>> quantityColumn = new MFXTableColumn<>("Quantidade", true, Comparator.comparing(map -> map.get("Quantidade").toString()));

        eanColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.get("EAN").toString()));
        skuColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.get("SKU").toString()));
        quantityColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.get("Quantidade").toString()));

        dataTable.getTableColumns().addAll(eanColumn, skuColumn, quantityColumn);
        dataTable.setItems(data);
    }

    /**
     * Define o Stage principal para exibição de diálogos.
     */
    public void setOwnerStage(Stage stage) {
        this.ownerStage = stage;
    }

    @FXML
    public void importSpreadsheet() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(ownerStage);
        if (file != null) {
            try {
                List<Map<String, Object>> importedData = spreadsheetReader.readSpreadsheet(file);
                data.clear();
                data.addAll(importedData);
                dataTable.setItems(data);
                CustomAlert.showInfoAlert(ownerStage, "Importação bem-sucedida", "Planilha importada com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
                CustomAlert.showErrorAlert(ownerStage, "Erro de Importação", "Ocorreu um erro ao importar a planilha.");
            }
        }
    }

    @FXML
    public void downloadTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("template.xlsx");
        File file = fileChooser.showSaveDialog(ownerStage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("EAN\tSKU\tQuantidade\n");
                writer.write("1234567890123\tSKU123\t10\n");
                writer.write("9876543210987\tSKU987\t20\n");

                CustomAlert.showInfoAlert(ownerStage, "Salvar Modelo de Planilha", "Modelo de planilha salvo com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
                CustomAlert.showErrorAlert(ownerStage, "Erro ao Salvar", "Ocorreu um erro ao salvar o modelo de planilha.");
            }
        }
    }

    @FXML
    public void analyzeData() {
        if (data.isEmpty()) {
            CustomAlert.showWarningAlert(ownerStage, "Dados Ausentes", "Nenhum dado foi importado para análise.");
            return;
        }

        StringBuilder analyzedData = new StringBuilder();
        for (Map<String, Object> row : data) {
            analyzedData.append("EAN: ").append(row.get("EAN"))
                    .append(", SKU: ").append(row.get("SKU"))
                    .append(", Quantidade: ").append(row.get("Quantidade"))
                    .append("\n");
        }
        outputArea.setText(analyzedData.toString());
    }

    @FXML
    public void printData() {
        String dataToPrint = outputArea.getText();
        if (dataToPrint.isEmpty()) {
            CustomAlert.showWarningAlert(ownerStage, "Sem Dados", "Nenhum dado analisado para impressão.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(ownerStage)) {
            boolean success = job.printPage(outputArea);
            if (success) {
                job.endJob();
                CustomAlert.showInfoAlert(ownerStage, "Impressão", "Dados impressos com sucesso!");
            } else {
                CustomAlert.showErrorAlert(ownerStage, "Erro de Impressão", "Ocorreu um erro ao imprimir os dados.");
            }
        }
    }
}
