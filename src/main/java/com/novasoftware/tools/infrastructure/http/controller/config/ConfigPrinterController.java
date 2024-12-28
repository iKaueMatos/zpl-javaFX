package com.novasoftware.tools.infrastructure.http.controller.config;

import com.novasoftware.tools.domain.service.PrinterService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ConfigPrinterController {

    @FXML
    private ComboBox<String> zplPrinterComboBox;

    @FXML
    private ComboBox<String> danfePrinterComboBox;

    private PrinterService printerService;

    @FXML
    public void initialize() {
        printerService = new PrinterService();

        populatePrintersComboBoxes();

        zplPrinterComboBox.setOnAction(event -> saveSelectedPrinter("zpl", zplPrinterComboBox.getValue()));
        danfePrinterComboBox.setOnAction(event -> saveSelectedPrinter("danfe", danfePrinterComboBox.getValue()));
    }

    private void populatePrintersComboBoxes() {
        zplPrinterComboBox.getItems().clear();
        danfePrinterComboBox.getItems().clear();

        for (var printService : printerService.getAvailablePrinters()) {
            String printerName = printService.getName();
            zplPrinterComboBox.getItems().add(printerName);
            danfePrinterComboBox.getItems().add(printerName);
        }

        if (!zplPrinterComboBox.getItems().isEmpty()) {
            zplPrinterComboBox.setValue(zplPrinterComboBox.getItems().get(0));
        }
        if (!danfePrinterComboBox.getItems().isEmpty()) {
            danfePrinterComboBox.setValue(danfePrinterComboBox.getItems().get(0));
        }
    }

    private void saveSelectedPrinter(String type, String printerName) {
        int index = getPrinterIndex(printerName);

        if (index != -1) {
            printerService.selectPrinter(index);
            System.out.println("Configuração salva para " + type + ": " + printerName);
        } else {
            System.out.println("Impressora inválida selecionada para " + type + ".");
        }
    }

    private int getPrinterIndex(String printerName) {
        for (int i = 0; i < printerService.getAvailablePrinters().size(); i++) {
            if (printerService.getAvailablePrinters().get(i).getName().equals(printerName)) {
                return i;
            }
        }
        return -1;
    }
}
