package com.novasoftware.tools.domain.service;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.PrinterName;

public class PrinterService {
    private PrintService selectedPrinter;
    private String printerIp;
    private int printerPort;
    private List<PrintService> availablePrinters;

    public PrinterService() {
        availablePrinters = new ArrayList<>();
    }

    public void detectPrinters() {
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

    public List<PrintService> getAvailablePrinters() {
        return availablePrinters;
    }

    public void selectPrinter(int index) {
        if (index >= 0 && index < availablePrinters.size()) {
            selectedPrinter = availablePrinters.get(index);
            String selectedPrinterName = getPrinterName(selectedPrinter);
            System.out.println("Impressora selecionada: " + selectedPrinterName);
        } else {
            System.out.println("Seleção inválida.");
        }
    }

    private String getPrinterName(PrintService printService) {
        PrintServiceAttributeSet attributes = printService.getAttributes();
        for (Attribute attr : attributes.toArray()) {
            if (attr instanceof PrinterName) {
                return ((PrinterName) attr).getValue();
            }
        }
        return "Desconhecido";
    }

    public void selectPrinterDialog() {
        if (availablePrinters.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma impressora disponível.", "Erro de Impressora", JOptionPane.ERROR_MESSAGE);
        } else {
            String[] printerNames = availablePrinters.stream()
                    .map(this::getPrinterName)
                    .toArray(String[]::new);

            String printerSelection = (String) JOptionPane.showInputDialog(null, "Selecione uma impressora",
                    "Seleção de Impressora", JOptionPane.QUESTION_MESSAGE,
                    null, printerNames, printerNames[0]);

            if (printerSelection != null) {
                int index = Arrays.asList(printerNames).indexOf(printerSelection);
                selectPrinter(index);
            } else {
                System.out.println("Nenhuma impressora selecionada.");
            }
        }
    }

    public PrintService getSelectedPrinter() {
        return selectedPrinter;
    }

    public String getPrinterIp() {
        return printerIp;
    }
}
