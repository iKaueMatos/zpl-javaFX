package com.novasoftware.config.infrastructure.http.controller.config;

import com.novasoftware.tools.domain.service.PrinterService;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ConfigPrinterController {

    @FXML
    private MFXComboBox<String> printA4;

    private PrinterService printerService;

    @FXML
    public void initialize() {
        printerService = new PrinterService();
        detectCommonPrinters();

        printA4.setOnAction(event -> saveSelectedPrinter("A4", printA4.getValue()));
    }

    private void detectCommonPrinters() {
        printerService.detectPrinters();
        populatePrintersComboBoxes();
    }

    private void populatePrintersComboBoxes() {
        boolean foundPrinter = false;

        for (var printService : printerService.getAvailablePrinters()) {
            String printerName = printService.getName();
            printA4.getItems().add(printerName);
            foundPrinter = true;
        }

        if (!foundPrinter) {
            printA4.setValue("Nenhuma impressora encontrada.");
        } else {
            if (!printA4.getItems().isEmpty()) {
                printA4.setValue(printA4.getItems().get(0));
            }
        }
    }

    private void saveSelectedPrinter(String type, String printerName) {
        if (printerName != null && !printerName.equals("Nenhuma impressora encontrada.") && !printerName.equals("Procurando impressoras...")) {
            printerService.savePrinterConfigurations(type, printerName);
        }
    }

    @FXML
    public void handleSave(ActionEvent actionEvent) {
        String danfePrinter = printA4.getValue();

        if (danfePrinter.equals("Nenhuma impressora encontrada.")) {
            System.out.println("Erro: Nenhuma impressora foi encontrada.");
        } else {
            saveSelectedPrinter("danfe", danfePrinter);
            System.out.println("Configurações salvas com sucesso!");
        }
    }
}
