package com.novasoftware.tools.domain.service;

import com.novasoftware.tools.application.repository.ConfigRepository;
import com.novasoftware.tools.domain.model.Config;
import com.novasoftware.tools.infrastructure.repository.ConfigRepositoryImpl;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.PrinterName;
import java.util.ArrayList;
import java.util.List;

public class PrinterService {
    private List<PrintService> availablePrinters;
    private List<String> zebraPrinters;

    private ConfigRepository configRepository;

    public PrinterService() {
        availablePrinters = new ArrayList<>();
        zebraPrinters = new ArrayList<>();
        configRepository = new ConfigRepositoryImpl();
    }

    public List<PrintService> getAvailablePrinters() {
        return availablePrinters;
    }

    public List<String> getPrinterNames() {
        List<String> printerNames = new ArrayList<>();
        for (PrintService printService : availablePrinters) {
            PrintServiceAttributeSet attributes = printService.getAttributes();
            for (Attribute attr : attributes.toArray()) {
                if (attr instanceof PrinterName) {
                    printerNames.add(((PrinterName) attr).getValue());
                }
            }
        }
        return printerNames;
    }

    public List<String> getZebraPrinters() {
        return zebraPrinters;
    }

    public void detectPrinters() {
        availablePrinters.clear();
        try {
            DocFlavor df = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(df, null);
            for (PrintService printService : printServices) {
                PrintServiceAttributeSet attributes = printService.getAttributes();
                for (Attribute attr : attributes.toArray()) {
                    if (attr instanceof PrinterName) {
                        String printerName = ((PrinterName) attr).getValue();
                        System.out.println("Impressora encontrada: " + printerName);
                        availablePrinters.add(printService);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePrinterConfigurations(String type, String printerName) {
        Config config = new Config();
        config.setType(type);
        config.setValue(printerName);

        configRepository.saveConfig(config);
        System.out.println("Configuração salva para " + type + ": " + printerName);
    }
}
