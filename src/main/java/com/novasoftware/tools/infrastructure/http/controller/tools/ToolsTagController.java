package com.novasoftware.tools.infrastructure.http.controller.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.novasoftware.base.controller.BaseController;
import com.novasoftware.core.Enum.LabelFormat;
import com.novasoftware.core.example.ZPLGenerateExample;
import com.novasoftware.core.http.client.LabelaryClient;
import com.novasoftware.tools.application.usecase.LabelGenerator;
import com.novasoftware.tools.domain.Enum.LabelConstants;
import com.novasoftware.tools.domain.Enum.LabelType;
import com.novasoftware.tools.domain.service.ZplFileService;

import com.novasoftware.tools.infrastructure.service.ZebraPrinterConfigurationService;
import com.novasoftware.tools.infrastructure.service.ZebraPrinterService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ToolsTagController extends BaseController implements Initializable {

    @FXML
    public ImageView imageView;

    @FXML
    public MFXButton printer;

    @FXML
    public MFXButton detectPrintersButton;

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

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private MFXComboBox<String> labelDimension;


    private final LabelGenerator labelGenerator = new LabelGenerator();
    private final ZplFileService zplFileService = new ZplFileService();

    private final ZebraPrinterConfigurationService zebraPrinterConfiguration = new ZebraPrinterConfigurationService();

    private final ZebraPrinterService zebraPrinterService = new ZebraPrinterService();

    @FXML
    private VBox imageContainer;

    @FXML
    private Label imageContainerLabel;

    public ToolsTagController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> formats = FXCollections.observableArrayList(
                LabelConstants.FORMAT_1_COLUMN,
                LabelConstants.FORMAT_2_COLUMNS,
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
        outputArea.setDisable(true);
        imageContainer.setVisible(false);
        imageContainerLabel.setVisible(false);
        imageContainer.setMinSize(300, 300);
        imageContainer.setPrefSize(400, 400);
        imageContainer.setMaxSize(500, 500);

        labelTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                toggleFieldsBasedOnLabelType(newVal);
                try {
                    showExampleForFormat(newVal);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        skuField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        eanField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        formatFieldComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> validateFields());
    }

    private void showExampleForFormat(String format) throws IOException, InterruptedException {
        loadingIndicator.setVisible(true);
        try {
            String exampleText;
            byte[] imageBytes;
            switch (format) {
                case LabelConstants.LABEL_CODE_128:
                    imageBytes = LabelaryClient.sendZplToLabelary(
                            ZPLGenerateExample.generateCode128("Exemplo"),
                            LabelFormat.PRINTER_DENSITY_8DPMM.getValue(),
                            LabelFormat.LABEL_DIMENSIONS_3X2.getValue(),
                            LabelFormat.LABEL_INDEX_0.getValue(),
                            LabelFormat.OUTPUT_FORMAT_IMAGE.getValue()
                    );
                    imageContainer.setVisible(true);
                    imageContainerLabel.setVisible(true);
                    exampleText = "Exemplo: Etiquetas organizadas em 2 colunas.";
                    break;
                case LabelConstants.LABEL_QR_CODE:
                    imageBytes = LabelaryClient.sendZplToLabelary(
                            ZPLGenerateExample.generateQRCode("Exemplo"),
                            LabelFormat.PRINTER_DENSITY_8DPMM.getValue(),
                            LabelFormat.LABEL_DIMENSIONS_3X2.getValue(),
                            LabelFormat.LABEL_INDEX_0.getValue(),
                            LabelFormat.OUTPUT_FORMAT_IMAGE.getValue()
                    );
                    imageContainer.setVisible(true);
                    imageContainerLabel.setVisible(true);
                    exampleText = "Exemplo: Etiquetas organizadas em 1 coluna.";
                    break;
                case LabelConstants.LABEL_EAN_13:
                    imageBytes = LabelaryClient.sendZplToLabelary(
                            ZPLGenerateExample.generateEAN13("7891186260103"),
                            LabelFormat.PRINTER_DENSITY_8DPMM.getValue(),
                            LabelFormat.LABEL_DIMENSIONS_3X2.getValue(),
                            LabelFormat.LABEL_INDEX_0.getValue(),
                            LabelFormat.OUTPUT_FORMAT_IMAGE.getValue()
                    );
                    imageContainer.setVisible(true);
                    imageContainerLabel.setVisible(true);
                    exampleText = "Exemplo: Página com 4 etiquetas idênticas.";
                    break;
                default:
                    imageBytes = null;
                    exampleText = "";
            }

            outputArea.setText(exampleText);

            if (imageBytes != null) {
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);
                imageContainer.getChildren().clear();
                imageContainer.getChildren().add(imageView);
            }
        } finally {
            loadingIndicator.setVisible(false);
        }
    }

    private void validateFields() {
        boolean allValid = !eanField.getText().isEmpty() &&
                !skuField.getText().isEmpty() &&
                !quantityField.getText().isEmpty() &&
                formatFieldComboBox.getValue() != null;

        saveButton.setDisable(!allValid);
    }

    private void toggleFieldsBasedOnLabelType(String labelType) {
        LabelType type = LabelType.fromString(labelType);

        if (type != null) {
            switch (type) {
                case EAN_13:
                case UPC_A:
                    eanField.setDisable(false);
                    skuField.setDisable(true);
                    skuField.setPromptText("Desabilitado");
                    eanField.setPromptText("Digite o EAN");
                    eanField.setAnimated(true);
                    skuField.clear();
                    break;

                case CODE_128:
                case CODE_39:
                    eanField.setDisable(true);
                    skuField.setDisable(false);
                    eanField.setPromptText("Desabilitado");
                    skuField.setPromptText("Digite o SKU");
                    eanField.clear();
                    break;

                case QR_CODE:
                    eanField.setDisable(true);
                    skuField.setDisable(false);
                    eanField.setPromptText("Desabilitado");
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
                    outputArea.setDisable(false);
                    outputArea.setText(zpl);
                    printer.setVisible(true);
                    detectPrintersButton.setVisible(true);
                    saveButton.setDisable(false);
                }
            } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        }
    }

    @FXML
    private void saveZplToFile() {
        try {
            zplFileService.saveZplToFile(outputArea.getText());
            showAlert(Alert.AlertType.INFORMATION, "Arquivo Salvo", "ZPL salvo com sucesso.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Não foi possível salvar o arquivo.");
        }
    }

    @FXML
    private void printLabel() {
        zebraPrinterService.printLabel(outputArea.getText());
    }

    @FXML
    private void onDetectPrintersButtonClicked(ActionEvent event) {
        zebraPrinterConfiguration.detectZebraPrinters();
    }
}
