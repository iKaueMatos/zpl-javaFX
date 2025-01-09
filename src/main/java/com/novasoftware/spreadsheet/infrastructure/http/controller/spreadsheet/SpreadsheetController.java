package com.novasoftware.spreadsheet.infrastructure.http.controller.spreadsheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.novasoftware.product.application.dto.ProductData;
import com.novasoftware.product.application.repository.ProductRepository;
import com.novasoftware.product.domain.service.ProductServiceImpl;
import com.novasoftware.product.infrastructure.repository.ProductRepositoryImpl;
import com.novasoftware.shared.util.alert.CustomAlert;
import com.novasoftware.spreadsheet.infrastructure.service.ImportSpreadsheetService;
import com.novasoftware.tools.application.usecase.LabelGeneratorService;
import com.novasoftware.tools.application.usecase.SpreadsheetReader;
import com.novasoftware.tools.domain.Enum.LabelConstants;
import com.novasoftware.tools.domain.service.ZplFileService;
import com.novasoftware.tools.infrastructure.service.TemplateDownloadSpreendsheetService;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SpreadsheetController implements Initializable {
    @FXML
    public MFXButton downloadErrorButton;

    @FXML
    public MFXButton saveButton;

    @FXML
    public MFXProgressSpinner loadingIndicator;

    @FXML
    private MFXTableView<Map<String, Object>> table;

    @FXML
    private MFXTableView<Map<String, Object>> historyTable;

    @FXML
    private MFXTableView<Map<String, Object>> errorLogTable;

    private String zpl;

    private final SpreadsheetReader spreadsheetReader;
    private final ObservableList<Map<String, Object>> data;
    private final ObservableList<Map<String, Object>> historyData;
    private final ObservableList<Map<String, Object>> errorLogData;
    private final LabelGeneratorService labelGeneratorService;

    private Stage ownerStage;

    public SpreadsheetController() {
        this.spreadsheetReader = new SpreadsheetReader();
        this.labelGeneratorService = new LabelGeneratorService();
        this.data = FXCollections.observableArrayList();
        this.historyData = FXCollections.observableArrayList();
        this.errorLogData = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilter();
        setupHistoryTable();
        downloadErrorButton.setVisible(false);
        saveButton.setVisible(false);
    }

    private void setupTable() {
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

        name.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("Nome", "N/A"))));
        eanColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("GTIN", "N/A"))));
        skuColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("SKU", "N/A"))));
        skuVariation.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("SKU_VARIACAO", "N/A"))));
        quantityColumn.setRowCellFactory(data -> new MFXTableRowCell<>(map -> String.valueOf(map.getOrDefault("Quantidade", "0"))));

        table.getTableColumns().addAll(name, eanColumn, skuColumn, skuVariation, quantityColumn);
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
            loadingIndicator.setVisible(true);
            ImportSpreadsheetService importService = new ImportSpreadsheetService(ownerStage, downloadErrorButton);

            importService.importSpreadsheet(file, productList -> {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);


                    String importDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    List<Map<String, Object>> mappedData = productList.stream()
                            .map(product -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("Nome", product.name());
                                map.put("SKU", product.sku());
                                map.put("SKU_VARIACAO", product.variationSku());
                                map.put("GTIN", product.ean());
                                map.put("Quantidade", product.quantity());
                                return map;
                            })
                            .collect(Collectors.toList());

                    data.addAll(mappedData);
                    table.setItems(data);
                    table.setCache(true);

                    Map<String, Object> historyEntry = new HashMap<>();
                    historyEntry.put("Data", importDate);
                    historyEntry.put("Status", "Importação bem-sucedida");

                    historyData.add(historyEntry);
                    historyTable.setItems(historyData);
                    showConfirmationModal(importDate, productList);
                });
            });
        }
    }

    private void showConfirmationModal(String importDate, List<ProductData> productList) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Importação");
        alert.setHeaderText("Você está prestes a salvar os dados no banco de dados.");
        alert.setContentText("Deseja realmente salvar os dados da importação?");
        Optional<ButtonType> result = alert.showAndWait();
        ProductServiceImpl productService = new ProductServiceImpl();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            productService.save(productList);
        } else {
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("Data", importDate);
            historyEntry.put("Status", "Importação cancelada");

            historyData.add(historyEntry);
            historyTable.setItems(historyData);
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


    public void showLabelFormatsModal(ActionEvent actionEvent) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Configurar Formatos de Etiqueta");

        VBox modalLayout = new VBox(15);
        modalLayout.setPadding(new Insets(10));
        modalLayout.setPrefWidth(600);
        modalLayout.setPrefHeight(600);

        MFXComboBox<String> labelFormatComboBox = new MFXComboBox<>();

        ObservableList<String> formats = FXCollections.observableArrayList(
                LabelConstants.FORMAT_1_COLUMN,
                LabelConstants.FORMAT_2_COLUMNS,
                LabelConstants.FORMAT_4_LABELS,
                LabelConstants.FORMAT_CUSTOM_SHIPPING
        );

        labelFormatComboBox.setItems(formats);
        labelFormatComboBox.setMaxWidth(Double.MAX_VALUE);
        MFXComboBox<String> labelTypeComboBox = new MFXComboBox<>();
        ObservableList<String> labelTypes = FXCollections.observableArrayList(
                LabelConstants.LABEL_CODE_128,
                LabelConstants.LABEL_CODE_39,
                LabelConstants.LABEL_EAN_13,
                LabelConstants.LABEL_UPC_A,
                LabelConstants.LABEL_QR_CODE
        );

        labelTypeComboBox.setItems(labelTypes);
        labelTypeComboBox.setMaxWidth(Double.MAX_VALUE);
        MFXComboBox<String> labelDataChoiceComboBox = new MFXComboBox<>();
        ObservableList<String> dataChoices = FXCollections.observableArrayList("SKU", "EAN");
        labelDataChoiceComboBox.setItems(dataChoices);
        labelDataChoiceComboBox.setMaxWidth(Double.MAX_VALUE);

        MFXButton generateLabelsButton = new MFXButton("Gerar Etiquetas");
        generateLabelsButton.setMaxWidth(Double.MAX_VALUE);
        generateLabelsButton.setStyle("-fx-background-color: #B916F9;  -fx-text-fill: #FFFFFF;");

        generateLabelsButton.setOnAction(event -> {
            try {
                generateLabels(labelFormatComboBox.getSelectedItem(), labelTypeComboBox.getSelectedItem(), labelDataChoiceComboBox.getSelectedItem());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            modalStage.close();
        });

        MFXButton closeButton = new MFXButton("Fechar");
        closeButton.setMaxWidth(Double.MAX_VALUE);
        closeButton.setOnAction(event -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #D32F2F;  -fx-text-fill: #FFFFFF;");

        modalLayout.getChildren().addAll(
                new Label("Escolha o Formato de Etiqueta:"),
                labelFormatComboBox,
                new Label("Escolha o Tipo de Etiqueta:"),
                labelTypeComboBox,
                new Label("Escolha o Tipo de Dado para Etiqueta:"),
                labelDataChoiceComboBox,
                generateLabelsButton,
                closeButton
        );

        Scene modalScene = new Scene(modalLayout, 600, 600);
        modalStage.setScene(modalScene);
        modalStage.setResizable(true);
        modalStage.show();
    }

    @FXML
    public void generateLabels(String labelFormat, String labelType, String labelDataChoice) throws IOException {
        if (labelFormat == null || labelType == null || labelDataChoice == null) {
            CustomAlert.showWarningAlert(ownerStage, "Seleção Inválida", "Por favor, selecione o formato, o tipo de etiqueta e o dado para gerar.");
            return;
        }

        List<Map<String, Object>> labelsData = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Map<String, Object> labelData = new HashMap<>();

            String dataToUse = labelDataChoice.equals("SKU") ?
                    String.valueOf(row.getOrDefault("SKU_VARIACAO", "N/A")) :
                    String.valueOf(row.getOrDefault("EAN", "N/A"));

            int quantity = Integer.parseInt(String.valueOf(row.getOrDefault("Quantidade", "0")));

            labelData.put(labelDataChoice, dataToUse);
            labelData.put("Quantidade", quantity);
            labelsData.add(labelData);
        }

        zpl = labelGeneratorService.generateZpl(labelsData, labelFormat, labelType);
        ZplFileService ZplFile = new ZplFileService();
        ZplFile.saveZplToFile(zpl);
        saveButton.setVisible(true);

        CustomAlert.showInfoAlert(ownerStage, "Etiquetas Geradas", "As etiquetas foram geradas com sucesso!");
    }

    @FXML
    public void downloadErrorFile() {
        File errorFile = new File("Planilha_Erros.xlsx");

        if (errorFile.exists()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
            fileChooser.setInitialFileName("Planilha_Erros.xlsx");

            File fileToSave = fileChooser.showSaveDialog(ownerStage);
            if (fileToSave != null) {
                try {
                    Files.copy(errorFile.toPath(), fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    CustomAlert.showInfoAlert(ownerStage, "Download Concluído", "A planilha de erros foi baixada com sucesso!");
                } catch (IOException e) {
                    CustomAlert.showErrorAlert(ownerStage, "Erro ao Baixar", "Ocorreu um erro ao tentar baixar a planilha de erros.");
                }
            }
        } else {
            CustomAlert.showWarningAlert(ownerStage, "Arquivo de Erros", "Não há planilha de erros para baixar.");
        }
    }

    @FXML
    public void saveZplToFile(ActionEvent actionEvent) {
        if (zpl == null || zpl.isEmpty()) {
            CustomAlert.showWarningAlert(ownerStage, "Erro", "Nenhum ZPL gerado para salvar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZPL Files", "*.zpl"));
        fileChooser.setInitialFileName("Etiquetas.zpl");

        File fileToSave = fileChooser.showSaveDialog(ownerStage);
        if (fileToSave != null) {
            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                fileOut.write(zpl.getBytes());
                CustomAlert.showInfoAlert(ownerStage, "Arquivo Salvo", "O arquivo ZPL foi salvo com sucesso!");
            } catch (IOException e) {
                CustomAlert.showErrorAlert(ownerStage, "Erro ao Salvar", "Ocorreu um erro ao tentar salvar o arquivo ZPL.");
            }
        }
    }
}