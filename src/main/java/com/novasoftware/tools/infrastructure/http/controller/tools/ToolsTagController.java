package com.novasoftware.tools.infrastructure.http.controller.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.print.PrintService;

import com.novasoftware.tools.application.usecase.LabelGenerator;
import com.novasoftware.tools.application.usecase.SpreadsheetReader;
import com.novasoftware.tools.domain.service.PrinterService;
import com.novasoftware.tools.domain.service.ZplFileService;
import com.novasoftware.tools.infrastructure.database.DatabaseManager;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.IntegerSpinnerModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ToolsTagController implements Initializable {
    @FXML
    private MFXTextField eanField;

    @FXML
    private MFXTextField skuField;

    @FXML
    private MFXTextField quantityField;

    @FXML
    private MFXComboBox<String> formatFieldComboBox;

    @FXML
    private MFXComboBox<String> labelTypeComboBox;

    @FXML
    private MFXButton generateButton;

    @FXML
    private MFXButton clearButton;

    @FXML
    private MFXButton printButton;

    @FXML
    private MFXButton printerSettingsButton;

    @FXML
    private MFXButton visualizeButton;

    @FXML
    private MFXButton saveButton;

    @FXML
    private MFXButton filterButton;

    @FXML
    private MFXTextField filterEanField;

    @FXML
    private MFXTextField filterSkuField;

    @FXML
    private MFXTextField filterQuantityField;

    @FXML
    private TextArea outputArea;

    @FXML
    private MFXTableView<Map<String, Object>> dataTable;

    @FXML
    private MFXTableColumn<Map<String, Object>> eanColumn;

    @FXML
    private MFXTableColumn<Map<String, Object>> skuColumn;

    @FXML
    private MFXTableColumn<Map<String, Object>> quantityColumn;

    @FXML
    private MFXSpinner<Integer> labelWidthSpinner;

    @FXML
    private MFXSpinner<Integer> labelHeightSpinner;

    @FXML
    private MFXSpinner<Integer> columnsSpinner;

    @FXML
    private MFXSpinner<Integer> rowsSpinner;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox menuContainer;

    @FXML
    private MFXButton menuButton;

    @FXML
    private MFXButton selectPrinterButton;

    @FXML
    private VBox printerSelectionBox;

    @FXML
    private MFXComboBox<String> printerComboBox;

    private final LabelGenerator labelGenerator;
    private final SpreadsheetReader spreadsheetReader;
    private final ZplFileService zplFileService;
    private final ObservableList<Map<String, Object>> data;
    private final PrinterService printerService;

    public ToolsTagController() {
        this.labelGenerator = new LabelGenerator();
        this.spreadsheetReader = new SpreadsheetReader();
        this.zplFileService = new ZplFileService();
        this.data = FXCollections.observableArrayList();
        this.printerService = new PrinterService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formatFieldComboBox.setItems(FXCollections.observableArrayList(
                "2-Colunas", "1-Coluna", "4-etiquetas por página", "Etiqueta Envio personalizado", "QRCode", "Code128"
        ));
        labelTypeComboBox.setItems(FXCollections.observableArrayList(
                "Code 128", "Code 39", "EAN-13", "UPC-A", "QR Code"
        ));

        saveButton.setDisable(true);

        labelWidthSpinner.setSpinnerModel(new IntegerSpinnerModel(100));
        labelHeightSpinner.setSpinnerModel(new IntegerSpinnerModel(100));
        columnsSpinner.setSpinnerModel(new IntegerSpinnerModel(1));
        rowsSpinner.setSpinnerModel(new IntegerSpinnerModel(1));

        labelWidthSpinner.setValue(400);
        labelHeightSpinner.setValue(150);
        columnsSpinner.setValue(2);
        rowsSpinner.setValue(2);

        selectPrinterButton.setOnAction(event -> showPrinterSelection());
        printerComboBox.setOnAction(event -> selectPrinter());
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
}