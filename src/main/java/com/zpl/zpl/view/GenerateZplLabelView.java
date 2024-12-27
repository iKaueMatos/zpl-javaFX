package com.zpl.zpl.view;

import com.zpl.zpl.application.usecase.LabelGenerator;
import com.zpl.zpl.application.usecase.SpreadsheetReader;
import com.zpl.zpl.domain.service.PrinterService;
import com.zpl.zpl.domain.service.ZplFileService;
import com.zpl.zpl.infrastructure.database.DatabaseManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.print.PrintService;

import javafx.event.ActionEvent;

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

    @FXML
    private Spinner<Integer> labelWidthSpinner;

    @FXML
    private Spinner<Integer> labelHeightSpinner;

    @FXML
    private Spinner<Integer> columnsSpinner;

    @FXML
    private Spinner<Integer> rowsSpinner;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox menuContainer;

    @FXML
    private Button menuButton;

    @FXML
    private Button selectPrinterButton;

    @FXML
    private VBox printerSelectionBox;

    @FXML
    private ComboBox<String> printerComboBox;

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
        saveButton.setDisable(true);

        labelWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000, 400));
        labelHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000, 150));
        columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2));
        rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2));

        selectPrinterButton.setOnAction(event -> showPrinterSelection());
        printerComboBox.setOnAction(event -> selectPrinter());
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
        if (!validateFields()) {
            return;
        }

        String format = formatFieldComboBox.getValue();
        String labelType = labelTypeComboBox.getValue();
        List<Map<String, Object>> eansAndSkus = data;

        if (eanField.getText().isEmpty() || skuField.getText().isEmpty() || quantityField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Dados Ausentes", "Por favor, preencha todos os campos: EAN, SKU e Quantidade.");
            return;
        }

        if (eansAndSkus.isEmpty()) {
            eansAndSkus = List.of(Map.of(
                "EAN", eanField.getText(),
                "SKU", skuField.getText(),
                "Quantidade", quantityField.getText()
            ));
        } else {
            eansAndSkus.add(Map.of(
                "EAN", eanField.getText(),
                "SKU", skuField.getText(),
                "Quantidade", quantityField.getText()
            ));
        }

        try {
            int labelWidth = labelWidthSpinner.getValue();
            int labelHeight = labelHeightSpinner.getValue();
            int columns = columnsSpinner.getValue();
            int rows = rowsSpinner.getValue();

            String zpl = labelGenerator.generateZpl(eansAndSkus, format, labelType, labelWidth, labelHeight, columns, rows);
            if (zplFileService.validateZplContent(zpl)) {
                outputArea.setText(zpl);
                visualizeButton.setDisable(false);
                saveButton.setDisable(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro de Validação", "O conteúdo ZPL gerado é inválido.");
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        }
    }

    private boolean validateFields() {
        if (formatFieldComboBox.getValue() == null || formatFieldComboBox.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", "Por favor, selecione um formato de etiqueta.");
            return false;
        }

        if (data.isEmpty() && (eanField.getText().isEmpty() || skuField.getText().isEmpty() || quantityField.getText().isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Dados Ausentes", "Nenhum dado foi carregado. Por favor, importe ou insira os dados necessários.");
            return false;
        }

        return true;
    }

    @FXML
    private void clearFields() {
        eanField.clear();
        skuField.clear();
        quantityField.clear();
        formatFieldComboBox.getSelectionModel().clearSelection();
        labelTypeComboBox.getSelectionModel().clearSelection();
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

    private void showPrinterSelection() {
        List<PrintService> printers = printerService.getAvailablePrinters();
        printerComboBox.getItems().clear();
        for (PrintService printer : printers) {
            printerComboBox.getItems().add(printer.getName());
        }
        printerSelectionBox.setVisible(true);
    }

    private void selectPrinter() {
        int selectedIndex = printerComboBox.getSelectionModel().getSelectedIndex();
        printerService.selectPrinter(selectedIndex);
        printerSelectionBox.setVisible(false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void downloadTemplate(ActionEvent event) {
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
}