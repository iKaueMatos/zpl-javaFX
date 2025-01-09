package com.novasoftware.config.infrastructure.http.controller.config;

import com.novasoftware.tools.domain.service.PrinterService;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ConfigPrinterController {

    @FXML
    private MFXComboBox<String> zebraPrinterComboBox;

    @FXML
    private MFXComboBox<String> printA4;

    private PrinterService printerService;

    @FXML
    public void initialize() {
        printerService = new PrinterService();
        detectZebraPrinters();
        detectCommonPrinters();

        zebraPrinterComboBox.setOnAction(event -> saveSelectedPrinter("zpl", zebraPrinterComboBox.getValue()));
        printA4.setOnAction(event -> saveSelectedPrinter("danfe", printA4.getValue()));
    }

    private void detectZebraPrinters() {
        printerService.detectZebraPrinters();
        populateZebraPrinterComboBox();
    }

    private void detectCommonPrinters() {
        printerService.detectPrinters();
        populatePrintersComboBoxes();
    }

    private void populateZebraPrinterComboBox() {
        zebraPrinterComboBox.getItems().clear();
        zebraPrinterComboBox.getItems().addAll(printerService.getZebraPrinters());

        if (zebraPrinterComboBox.getItems().isEmpty()) {
            zebraPrinterComboBox.setValue("Nenhuma impressora Zebra encontrada.");
        } else {
            zebraPrinterComboBox.setValue(zebraPrinterComboBox.getItems().get(0));
        }
    }

    private void populatePrintersComboBoxes() {
        boolean foundPrinter = false;

        for (var printService : printerService.getAvailablePrinters()) {
            String printerName = printService.getName();
            zebraPrinterComboBox.getItems().add(printerName);
            printA4.getItems().add(printerName);
            foundPrinter = true;
        }

        if (!foundPrinter) {
            printA4.setValue("Nenhuma impressora encontrada.");
            zebraPrinterComboBox.setValue("Nenhuma impressora Zebra encontrada.");
        } else {
            if (!zebraPrinterComboBox.getItems().isEmpty()) {
                zebraPrinterComboBox.setValue(zebraPrinterComboBox.getItems().get(0));
            }
            if (!printA4.getItems().isEmpty()) {
                printA4.setValue(printA4.getItems().get(0));
            }
        }
    }

    private void saveSelectedPrinter(String type, String printerName) {
        if (printerName != null && !printerName.equals("Nenhuma impressora encontrada.") && !printerName.equals("Nenhuma impressora Zebra encontrada.") && !printerName.equals("Procurando impressoras...")) {
            printerService.savePrinterConfigurations(type, printerName);
        }
    }

    @FXML
    public void handleSave(ActionEvent actionEvent) {
        String zplPrinter = zebraPrinterComboBox.getValue();
        String danfePrinter = printA4.getValue();

        if (zplPrinter.equals("Nenhuma impressora Zebra encontrada.") || danfePrinter.equals("Nenhuma impressora encontrada.")) {
            System.out.println("Erro: Nenhuma impressora foi encontrada.");
        } else {
            saveSelectedPrinter("zpl", zplPrinter);
            saveSelectedPrinter("danfe", danfePrinter);
            System.out.println("Configurações salvas com sucesso!");
        }
    }
}
