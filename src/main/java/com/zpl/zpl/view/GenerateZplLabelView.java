package com.zpl.zpl.view;

import com.zpl.zpl.domain.service.PrinterService;
import com.zpl.zpl.domain.service.ZplFileService;
import com.zpl.zpl.usecase.LabelGenerator;
import com.zpl.zpl.usecase.SpreadsheetReader;
import com.zpl.zpl.infrastructure.database.DatabaseManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GenerateZplLabelView implements Initializable {
    @FXML
    private TextField eanField;
    @FXML
    private TextField skuField;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox<String> formatFieldComboBox;
    @FXML
    private ComboBox<String> labelTypeComboBox;
    @FXML
    private CheckBox option1CheckBox;
    @FXML
    private CheckBox option2CheckBox;
    @FXML
    private CheckBox option3CheckBox;
    @FXML
    private Button generateButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button printButton;
    @FXML
    private Button printerSettingsButton;
    @FXML
    private Button visualizeButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button filterButton;
    @FXML
    private TextField filterEanField;
    @FXML
    private TextField filterSkuField;
    @FXML
    private TextField filterQuantityField;
    @FXML
    private TextArea outputArea;
    @FXML
    private TableView<Map<String, Object>> dataTable;
    @FXML
    private TableColumn<Map<String, Object>, String> eanColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> skuColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> quantityColumn;

    private final LabelGenerator labelGenerator;
    private final SpreadsheetReader spreadsheetReader;
    private final ZplFileService zplFileService;
    private final ObservableList<Map<String, Object>> data;
    private final PrinterService printerService;

    public GenerateZplLabelView() {
        this.labelGenerator = new LabelGenerator();
        this.spreadsheetReader = new SpreadsheetReader();
        this.zplFileService = new ZplFileService();
        this.data = FXCollections.observableArrayList();
        this.printerService = new PrinterService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formatFieldComboBox.setItems(FXCollections.observableArrayList("2-Colunas", "1-Coluna", "4-etiquetas por página", "Entiqueta Envio personalizado", "QRCode", "Code128"));
        labelTypeComboBox.setItems(FXCollections.observableArrayList("Code 128", "Code 39", "EAN-13", "UPC-A", "QR Code"));

        eanColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("EAN").toString()));
        skuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("SKU").toString()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("Quantidade").toString()));

        dataTable.setItems(data);
        visualizeButton.setDisable(true);
        saveButton.setDisable(true);
    }

    @FXML
    private void importSpreadsheet() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                List<Map<String, Object>> importedData = spreadsheetReader.readSpreadsheet(file);
                data.clear();
                data.addAll(importedData);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Importação bem-sucedida");
                alert.setHeaderText(null);
                alert.setContentText("Planilha importada com sucesso!");
                alert.showAndWait();

                eanField.setDisable(true);
                skuField.setDisable(true);
                quantityField.setDisable(true);
                if (!importedData.isEmpty()) {
                    quantityField.setText(importedData.get(0).get("Quantidade").toString());
                }
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
    private void generateLabel() {
        String format = formatFieldComboBox.getValue();
        List<Map<String, Object>> eansAndSkus = data;

        try {
            String zpl = labelGenerator.generateZpl(eansAndSkus, format);
            if (zplFileService.validateZplContent(zpl)) {
                outputArea.setText(zpl);
                visualizeButton.setDisable(false);
                saveButton.setDisable(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro de Validação");
                alert.setHeaderText(null);
                alert.setContentText("O conteúdo ZPL gerado é inválido.");
                alert.showAndWait();
            }
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Validação");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void clearFields() {
        eanField.clear();
        skuField.clear();
        quantityField.clear();
        formatFieldComboBox.getSelectionModel().clearSelection();
        labelTypeComboBox.getSelectionModel().clearSelection();
        option1CheckBox.setSelected(false);
        option2CheckBox.setSelected(false);
        option3CheckBox.setSelected(false);
        outputArea.clear();
        data.clear();

        // Enable fields
        eanField.setDisable(false);
        skuField.setDisable(false);
        quantityField.setDisable(false);
        visualizeButton.setDisable(true);
        saveButton.setDisable(true);
    }

    @FXML
    private void printLabel() {
        String zpl = outputArea.getText();
        if (!zpl.isEmpty()) {
            if (printerService.getSelectedPrinter() != null || printerService.getPrinterIp() != null) {
                if (printerService.printZplDocument(zpl)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Impressão");
                    alert.setHeaderText(null);
                    alert.setContentText("Etiqueta impressa com sucesso!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro de Impressão");
                    alert.setHeaderText(null);
                    alert.setContentText("Ocorreu um erro ao imprimir a etiqueta.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Impressora Não Selecionada");
                alert.setHeaderText(null);
                alert.setContentText("Nenhuma impressora foi selecionada.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sem ZPL");
            alert.setHeaderText(null);
            alert.setContentText("Nenhum conteúdo ZPL gerado para impressão.");
            alert.showAndWait();
        }
    }

    @FXML
    private void openPrinterSettings() {
        if (printerService.getSelectedPrinter() != null || printerService.getPrinterIp() != null) {
            try {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null && job.showPrintDialog(null)) {
                    job.endJob();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro ao Abrir Configurações da Impressora");
                alert.setHeaderText(null);
                alert.setContentText("Não foi possível abrir as configurações da impressora.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Impressora Não Selecionada");
            alert.setHeaderText(null);
            alert.setContentText("Nenhuma impressora foi selecionada.");
            alert.showAndWait();
        }
    }

    @FXML
    private void visualizeZpl() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VisualizeZplView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            VisualizeZplView controller = loader.getController();
            controller.setZplContent(outputArea.getText());
            stage.setTitle("Visualize ZPL");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveZplToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZPL Files", "*.zpl"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(outputArea.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Salvar ZPL");
                alert.setHeaderText(null);
                alert.setContentText("ZPL salvo com sucesso!");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro ao Salvar");
                alert.setHeaderText(null);
                alert.setContentText("Ocorreu um erro ao salvar o ZPL.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void filterData() {
        String eanFilter = filterEanField.getText();
        String skuFilter = filterSkuField.getText();
        String quantityFilter = filterQuantityField.getText();

        List<Map<String, Object>> filteredData = DatabaseManager.fetchFilteredData(eanFilter, skuFilter, quantityFilter);
        data.clear();
        data.addAll(filteredData);
    }
}