package com.novasoftware.tools.infrastructure.http.controller.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.novasoftware.tools.application.usecase.LabelGenerator;
import com.novasoftware.tools.domain.service.PrinterService;
import com.novasoftware.tools.domain.service.ZplFileService;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
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
    private MFXButton saveButton;

    @FXML
    private TextArea outputArea;

    private final LabelGenerator labelGenerator;
    private final ZplFileService zplFileService;
    private final ObservableList<Map<String, Object>> data;
    private final PrinterService printerService;

    public ToolsTagController() {
        this.labelGenerator = new LabelGenerator();
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
            String zpl = labelGenerator.generateZpl(eansAndSkus, format, labelType);
            if (zplFileService.validateZplContent(zpl)) {
                outputArea.setText(zpl);
                saveButton.setDisable(false);
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

        eanField.setDisable(false);
        skuField.setDisable(false);
        quantityField.setDisable(false);
        saveButton.setDisable(true);
    }

    @FXML
    private void printLabel() {
        String zpl = outputArea.getText();
        if (!zpl.isEmpty()) {
            if (printerService.getSelectedPrinter() != null || printerService.getPrinterIp() != null) {
                if (printerService.printZplDocument(zpl)) {
                    showAlert(Alert.AlertType.INFORMATION, "Impressão", "Etiqueta impressa com sucesso!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erro de Impressão", "Ocorreu um erro ao imprimir a etiqueta.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Impressora Não Selecionada", "Nenhuma impressora foi selecionada.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sem ZPL", "Nenhum conteúdo ZPL gerado para impressão.");
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
                showAlert(Alert.AlertType.INFORMATION, "Salvar ZPL", "ZPL salvo com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Ocorreu um erro ao salvar o ZPL.");
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