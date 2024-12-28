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

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SpreadsheetController implements Initializable {
    @FXML
    private MFXTableView<Map<String, Object>> dataTable;

    private final SpreadsheetReader spreadsheetReader;
    private final ObservableList<Map<String, Object>> data;

    @FXML
    private TextArea outputArea;

    public SpreadsheetController() {
        this.spreadsheetReader = new SpreadsheetReader();
        this.data = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MFXTableColumn<Map<String, Object>> eanColumn = new MFXTableColumn<>("EAN", true, Comparator.comparing(map -> map.get("EAN").toString()));
        MFXTableColumn<Map<String, Object>> skuColumn = new MFXTableColumn<>("SKU", true, Comparator.comparing(map -> map.get("SKU").toString()));
        MFXTableColumn<Map<String, Object>> quantityColumn = new MFXTableColumn<>("Quantidade", true, Comparator.comparing(map -> map.get("Quantidade").toString()));

        eanColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.get("EAN").toString()));
        skuColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.get("SKU").toString()));
        quantityColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.get("Quantidade").toString()));

        dataTable.getTableColumns().addAll(eanColumn, skuColumn, quantityColumn);
        dataTable.setItems(data);
    }

    @FXML
    public void importSpreadsheet() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                List<Map<String, Object>> importedData = spreadsheetReader.readSpreadsheet(file);
                data.clear();
                data.addAll(importedData);
                dataTable.setItems(data);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Importação bem-sucedida");
                alert.setHeaderText(null);
                alert.setContentText("Planilha importada com sucesso!");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro de Importação");
                alert.setHeaderText(null);
                alert.setContentText("Ocorreu um erro ao importar a planilha.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void downloadTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("template.xlsx");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("EAN\tSKU\tQuantidade\n");
                writer.write("1234567890123\tSKU123\t10\n");
                writer.write("9876543210987\tSKU987\t20\n");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Salvar Modelo de Planilha");
                alert.setHeaderText(null);
                alert.setContentText("Modelo de planilha salvo com sucesso!");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro ao Salvar");
                alert.setHeaderText(null);
                alert.setContentText("Ocorreu um erro ao salvar o modelo de planilha.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void analyzeData() {
        if (data.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Dados Ausentes", "Nenhum dado foi importado para análise.");
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
            showAlert(Alert.AlertType.WARNING, "Sem Dados", "Nenhum dado analisado para impressão.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(new Stage())) {
            boolean success = job.printPage(outputArea);
            if (success) {
                job.endJob();
                showAlert(Alert.AlertType.INFORMATION, "Impressão", "Dados impressos com sucesso!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro de Impressão", "Ocorreu um erro ao imprimir os dados.");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
