package com.novasoftware.tools.infrastructure.http.controller.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.novasoftware.base.controller.BaseController;
import com.novasoftware.tools.application.usecase.LabelGenerator;
import com.novasoftware.tools.domain.Enum.LabelConstants;
import com.novasoftware.tools.domain.Enum.LabelType;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ToolsTagController extends BaseController implements Initializable {

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
    private TextArea outputArea;

    @FXML
    private MFXButton saveButton;

    public VBox dynamicFieldsContainer;

    private final LabelGenerator labelGenerator;
    private final ZplFileService zplFileService;
    private final PrinterService printerService;

    public ToolsTagController() {
        this.labelGenerator = new LabelGenerator();
        this.zplFileService = new ZplFileService();
        this.printerService = new PrinterService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> formats = FXCollections.observableArrayList(
                LabelConstants.FORMAT_2_COLUMNS,
                LabelConstants.FORMAT_1_COLUMN,
                LabelConstants.FORMAT_4_LABELS,
                LabelConstants.FORMAT_CUSTOM_SHIPPING
        );

        ObservableList<String> labelTypes = FXCollections.observableArrayList(
                LabelConstants.LABEL_CODE_128,
                LabelConstants.LABEL_CODE_39,
                LabelConstants.LABEL_EAN_13,
                LabelConstants.LABEL_UPC_A,
                LabelConstants.LABEL_QR_CODE
        );

        formatFieldComboBox.setItems(formats);
        labelTypeComboBox.setItems(labelTypes);

        saveButton.setDisable(true);

        formatFieldComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showExampleForFormat(newVal);
            }
        });

        labelTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                toggleFieldsBasedOnLabelType(newVal);
            }
        });

        skuField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        eanField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        formatFieldComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> validateFields());
    }


    private void showExampleForFormat(String format) {
        String exampleText;
        String skuOrEan = skuField.getText().isEmpty() ? eanField.getText() : skuField.getText();

        switch (format) {
            case "2-Colunas - Etiquetas em duas colunas por página":
                exampleText = "Exemplo: Etiquetas organizadas em 2 colunas.";
                break;
            case "1-Coluna - Etiquetas em uma única coluna por página":
                exampleText = "Exemplo: Etiquetas organizadas em 1 coluna.";
                break;
            case "4-etiquetas por página - Padrão de 4 etiquetas iguais por página":
                exampleText = "Exemplo: Página com 4 etiquetas idênticas.";
                break;
            case "Etiqueta Envio personalizado - Formato para envio com dados customizados":
                exampleText = "Exemplo: Etiqueta personalizada para envio.";
                break;
            case "QRCode - Etiqueta com QR Code":
                exampleText = "Exemplo: Etiqueta com QR Code para escaneamento.";
                break;
            case "Code128 - Etiqueta com código de barras Code 128":
                exampleText = "Exemplo: Código de barras padrão Code 128.";
                break;
            default:
                exampleText = "";
        }
        outputArea.setText(exampleText);
    }

    private void toggleFieldsBasedOnLabelType(String labelType) {
        LabelType type = LabelType.fromString(labelType);

        if (type != null) {
            switch (type) {
                case EAN_13:
                case UPC_A:
                    eanField.setDisable(false);
                    skuField.setDisable(true);
                    eanField.setPromptText("Digite o EAN");
                    skuField.clear();
                    break;

                case CODE_128:
                case CODE_39:
                    eanField.setDisable(true);
                    skuField.setDisable(false);
                    skuField.setPromptText("Digite o SKU");
                    eanField.clear();
                    break;

                case QR_CODE:
                    eanField.setDisable(true);
                    skuField.setDisable(false);
                    skuField.setPromptText("Digite os dados para o QR Code");
                    eanField.clear();
                    break;

                default:
                    eanField.setDisable(false);
                    skuField.setDisable(false);
            }
        }
    }

    @FXML
    private void generateLabel() {
        try {
            String zpl = labelGenerator.generateZpl(
                    List.of(Map.of(
                            "EAN", eanField.getText(),
                            "SKU", skuField.getText(),
                            "Quantidade", quantityField.getText()
                    )),
                    formatFieldComboBox.getValue(),
                    labelTypeComboBox.getValue()
            );

            if (zplFileService.validateZplContent(zpl)) {
                outputArea.setText(zpl);
                saveButton.setDisable(false);
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        }
    }

    private void validateFields() {
        boolean allValid = !eanField.getText().isEmpty() &&
                !skuField.getText().isEmpty() &&
                !quantityField.getText().isEmpty() &&
                formatFieldComboBox.getValue() != null;

        saveButton.setDisable(!allValid);
    }

    @FXML
    private void saveZplToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZPL Files", "*.zpl"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(outputArea.getText());
                showAlert(Alert.AlertType.INFORMATION, "Arquivo Salvo", "ZPL salvo com sucesso.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível salvar o arquivo.");
            }
        }
    }
}
