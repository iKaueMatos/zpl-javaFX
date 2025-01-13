package com.novasoftware.tools.infrastructure.http.controller.tools;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.novasoftware.base.layout.BaseController;
import com.novasoftware.core.Enum.LabelFormat;
import com.novasoftware.core.example.ZPLGenerateExample;
import com.novasoftware.core.http.client.LabelaryClient;
import com.novasoftware.shared.util.alert.CustomAlert;
import com.novasoftware.tools.application.usecase.LabelGeneratorService;
import com.novasoftware.tools.domain.Enum.LabelConstants;
import com.novasoftware.tools.domain.Enum.LabelType;
import com.novasoftware.tools.domain.service.ImageZoomService;
import com.novasoftware.tools.domain.service.ZplFileService;

import com.novasoftware.tools.infrastructure.service.PDFViewerService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;

public class ToolsTagController extends BaseController implements Initializable {

    @FXML
    public ImageView imageView;

    @FXML
    public MFXButton printer;

    @FXML
    public MFXButton detectPrintersButton;

    @FXML
    public VBox imageViewContainer;

    @FXML
    public MFXComboBox<String> labelDpmm;

    @FXML
    public MFXButton downloadLabelPDF;

    @FXML
    public MFXButton downloadImageButton;

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

    @FXML
    private VBox imageContainer;

    private final LabelGeneratorService labelGenerator = new LabelGeneratorService();
    private final ZplFileService zplFileService = new ZplFileService();

    private final ImageZoomService imageZoomService = new ImageZoomService();

    private final PDFViewerService pdfViewerService = new PDFViewerService();

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
        labelTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                toggleFieldsBasedOnLabelType(newVal);
            }
        });

        skuField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        eanField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        formatFieldComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> validateFields());
        downloadLabelPDF.setVisible(false);
        downloadImageButton.setVisible(false);

        try {
            showExampleForFormat(LabelConstants.LABEL_CODE_128);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro ao carregar exemplo inicial", e);
        }
    }

    private void showExampleForFormat(String format) throws IOException, InterruptedException {
        loadingIndicator.setVisible(true);
        try {
            String zpl = "";
            byte[] imageBytes;
            switch (format) {
                case LabelConstants.LABEL_CODE_128:
                    String zplGenerateExampleCode128 = ZPLGenerateExample.generateCode128("Exemplo");
                    imageBytes = LabelaryClient.sendZplToLabelary(
                            zplGenerateExampleCode128,
                            LabelFormat.PRINTER_DENSITY_8DPMM.getValue(),
                            LabelFormat.LABEL_DIMENSIONS_3X2.getValue(),
                            LabelFormat.LABEL_INDEX_0.getValue(),
                            LabelFormat.OUTPUT_FORMAT_IMAGE.getValue()
                    );
                    zpl = zplGenerateExampleCode128;
                    break;
                case LabelConstants.LABEL_QR_CODE:
                    String zplGenerateExampleQrCode = ZPLGenerateExample.generateQRCode("Exemplo");
                    imageBytes = LabelaryClient.sendZplToLabelary(
                            zplGenerateExampleQrCode,
                            LabelFormat.PRINTER_DENSITY_8DPMM.getValue(),
                            LabelFormat.LABEL_DIMENSIONS_3X2.getValue(),
                            LabelFormat.LABEL_INDEX_0.getValue(),
                            LabelFormat.OUTPUT_FORMAT_IMAGE.getValue()
                    );
                    zpl = zplGenerateExampleQrCode;
                    break;
                case LabelConstants.LABEL_EAN_13:
                    String zplGenerateExampleEan = ZPLGenerateExample.generateEAN13("7891186260103");
                    imageBytes = LabelaryClient.sendZplToLabelary(
                            zplGenerateExampleEan,
                            LabelFormat.PRINTER_DENSITY_8DPMM.getValue(),
                            LabelFormat.LABEL_DIMENSIONS_3X2.getValue(),
                            LabelFormat.LABEL_INDEX_0.getValue(),
                            LabelFormat.OUTPUT_FORMAT_IMAGE.getValue()
                    );
                    imageContainer.setVisible(true);
                    zpl = zplGenerateExampleEan;
                    break;
                default:
                    imageBytes = null;
            }

            outputArea.setText(zpl);
            if (imageBytes != null) {
                Image image = new Image(new ByteArrayInputStream(imageBytes));

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(558.0);
                imageView.setFitHeight(300.0);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                imageViewContainer.getChildren().add(imageView);
                imageZoomService.setupImageZoom(imageViewContainer, imageView);
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

            labelDpmm.setValue(labelDpmm.getValue() != null ? labelDpmm.getValue() : LabelFormat.PRINTER_DENSITY_8DPMM.getValue());
            labelDimension.setValue(labelDimension.getValue() != null ? labelDimension.getValue() : LabelFormat.LABEL_DIMENSIONS_3X2.getValue());

            if (Integer.parseInt(quantityField.getText()) > 50) {
                CustomAlert.showErrorAlert(stage, "Ocorreu um erro", "A quantidade inserida não pode ser maior que 50.");
            }

            byte[] imageBytes = LabelaryClient.sendZplToLabelary(
                    zpl,
                    labelDpmm.getValue(),
                    labelDimension.getValue(),
                    LabelFormat.LABEL_INDEX_0.getValue(),
                    LabelFormat.OUTPUT_FORMAT_IMAGE.getValue()
            );

            if (imageBytes != null) {
                InputStream byteInputStream = new ByteArrayInputStream(imageBytes);
                BufferedImage bufferedImage = ImageIO.read(byteInputStream);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                Image image = new Image(inputStream);

                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setFitWidth(800);
                imageView.setFitHeight(600);

                imageViewContainer.getChildren().clear();
                imageZoomService.setupImageZoom(imageViewContainer, imageView);
                downloadImageButton.setVisible(true);
                downloadImageButton.setOnAction(event -> downloadImage(image));

                downloadLabelPDF.setVisible(true);
            }

            if (zplFileService.validateZplContent(zpl)) {
                outputArea.setText(zpl);
                saveButton.setDisable(false);
            }
        } catch (IllegalArgumentException e) {
            CustomAlert.showErrorAlert(stage, "Ocorreu um erro", "Erro de validação");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void saveZplToFile() {
        try {
            zplFileService.saveZplToFile(outputArea.getText());
            CustomAlert.showInfoAlert(stage, "Arquivo Salvo", "ZPL salvo com sucesso.");
        } catch (IOException e) {
            CustomAlert.showErrorAlert(stage, "Erro ao Salvar","Não foi possível salvar o arquivo.");
        }
    }

    @FXML
    private void downloadLabelPDF() throws IOException {
        String zpl = outputArea.getText();

        byte[] pdfBytes = pdfViewerService.pdfTag(
                zpl,
                labelDpmm.getValue(),
                labelDimension.getValue(),
                LabelFormat.LABEL_INDEX_0.getValue(),
                LabelFormat.OUTPUT_FORMAT_PDF.getValue()
        );

        File tempPdf = File.createTempFile("etiqueta", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempPdf)) {
            fos.write(pdfBytes);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("etiqueta.pdf");

        File chosenFile = fileChooser.showSaveDialog(stage);
        if (chosenFile != null) {
            try (FileOutputStream fos = new FileOutputStream(chosenFile)) {
                fos.write(pdfBytes);
            } catch (IOException e) {
                CustomAlert.showErrorAlert(stage, "Erro ao salvar", "Não foi possivel salvar o pdf");
            }
        }
    }

    @FXML
    private void downloadImage(Image image) {
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar Imagem");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

            File defaultDirectory = new File(System.getProperty("user.home"));
            fileChooser.setInitialDirectory(defaultDirectory);

            File file = fileChooser.showSaveDialog(downloadImageButton.getScene().getWindow());
            if (file != null) {
                ImageIO.write(bufferedImage, "png", file);
                CustomAlert.showConfirmationAlert(stage, "Sucesso", "Imagem salva com sucesso.", () -> {
                    System.out.println("Imagem salva com sucesso!");
                });
            }
        } catch (IOException e) {
            CustomAlert.showErrorAlert(stage, "Erro ao salvar", "Ocorreu um erro interno do servidor");
        }
    }
}
