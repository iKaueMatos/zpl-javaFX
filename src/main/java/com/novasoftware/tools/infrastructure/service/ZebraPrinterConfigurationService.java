package com.novasoftware.tools.infrastructure.service;

import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryException;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.NetworkDiscoverer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ZebraPrinterConfigurationService {
    private static List<String> printersFound = new ArrayList<>();

    public void detectZebraPrinters() {
        try {
            DiscoveryHandler discoveryHandler = new DiscoveryHandler() {
                @Override
                public void foundPrinter(DiscoveredPrinter discoveredPrinter) {
                    printersFound.add(discoveredPrinter.address);
                    System.out.println("Impressora encontrada: " + discoveredPrinter.address);
                }

                @Override
                public void discoveryFinished() {
                    if (printersFound.isEmpty()) {
                        System.out.println("Nenhuma impressora Zebra encontrada.");
                    } else {
                        System.out.println("Descoberta de impressoras concluída.");
                    }
                }

                @Override
                public void discoveryError(String errorMessage) {
                    System.out.println("Erro ao descobrir impressoras: " + errorMessage);
                }
            };

            NetworkDiscoverer.findPrinters(discoveryHandler);
        } catch (DiscoveryException e) {
            e.printStackTrace();
            System.out.println("Erro ao detectar impressoras.");
        }
    }

    public static String selectPrinter() {
        if (printersFound.isEmpty()) {
            System.out.println("Nenhuma impressora Zebra encontrada.");
            return null;
        }

        String[] printerIps = printersFound.toArray(new String[0]);

        String selectedIp = (String) JOptionPane.showInputDialog(null,
                "Selecione uma impressora Zebra:",
                "Seleção de Impressora",
                JOptionPane.PLAIN_MESSAGE,
                null,
                printerIps,
                printerIps[0]);

        return selectedIp;
    }

    public static void printLabel(String printerIp, String zplCommand) {
        try {
            TcpConnection connection = new TcpConnection(printerIp, 9100);
            connection.open();
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
            printer.sendCommand(zplCommand);

            connection.close();
            System.out.println("Etiqueta enviada para a impressora: " + printerIp);
        } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
            e.printStackTrace();
            System.out.println("Erro ao imprimir na impressora.");
        }
    }
}
