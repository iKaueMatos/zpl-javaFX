package com.zpl.zpl.domain.service;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.PrinterName;
import javax.usb.*;

import java.util.Scanner;

public class PrinterService {
    private PrintService selectedPrinter;
    private String printerIp;
    private int printerPort;

    public PrinterService() {
        detectPrinters();
    }

    public void detectPrinters() {
        try {
            List<PrintService> availablePrinters = new ArrayList<>();
            DocFlavor df = DocFlavor.BYTE_ARRAY.AUTOSENSE;

            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(df, null);
            for (PrintService printService : printServices) {
                PrintServiceAttributeSet attributes = printService.getAttributes();
                for (Attribute attr : attributes.toArray()) {
                    if (attr instanceof PrinterName) {
                        String printerName = ((PrinterName) attr).getValue();
                        System.out.println("Impressora encontrada na rede: " + printerName);
                        availablePrinters.add(printService);
                    }
                }
            }

            UsbServices services = UsbHostManager.getUsbServices();
            UsbHub rootHub = services.getRootUsbHub();
            detectUsbPrinters(rootHub);

            selectPrinterFromList(availablePrinters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detectUsbPrinters(UsbHub hub) throws UsbException {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            if (device.isUsbHub()) {
                detectUsbPrinters((UsbHub) device);
            } else {
                UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
                if (descriptor.idVendor() == 0x0A5F) { // Zebra vendor ID
                    System.out.println("Impressora Zebra USB detectada: " + device);
                    selectedPrinter = null;
                    printerIp = null;
                    printerPort = -1;
                }
            }
        }
    }

    private void selectPrinterFromList(List<PrintService> availablePrinters) {
        if (availablePrinters.isEmpty()) {
            System.out.println("Nenhuma impressora encontrada.");
            return;
        }

        System.out.println("Selecione uma impressora:");
        for (int i = 0; i < availablePrinters.size(); i++) {
            PrintService printService = availablePrinters.get(i);
            String printerName = getPrinterName(printService);
            System.out.println((i + 1) + ". " + printerName);
        }

        Scanner scanner = new Scanner(System.in);
        int selection = scanner.nextInt() - 1;
        if (selection >= 0 && selection < availablePrinters.size()) {
            selectedPrinter = availablePrinters.get(selection);
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

    private String getPrinterIp(PrintService printService) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(printService.getName());
            if (addresses.length > 0) {
                return addresses[0].getHostAddress();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized boolean printDocument(String texto) {
        if (selectedPrinter == null) {
            System.out.println("Nenhuma impressora foi selecionada.");
            return false;
        } else {
            try {
                DocPrintJob dpj = selectedPrinter.createPrintJob();
                InputStream stream = new ByteArrayInputStream(texto.getBytes());
                DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                Doc doc = new SimpleDoc(stream, flavor, null);
                dpj.print(doc, null);
                return true;
            } catch (PrintException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public synchronized boolean printDocument(byte[] bytes) {
        if (selectedPrinter == null) {
            System.out.println("Nenhuma impressora foi selecionada.");
            return false;
        } else {
            try {
                DocPrintJob dpj = selectedPrinter.createPrintJob();
                InputStream stream = new ByteArrayInputStream(bytes);
                DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                Doc doc = new SimpleDoc(stream, flavor, null);
                dpj.print(doc, null);
                return true;
            } catch (PrintException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // Imprime um documento ZPL para impressoras Zebra
    public synchronized boolean printZplDocument(String zplContent) {
        if (printerIp != null && printerPort > 0) {
            try (Socket socket = new Socket(printerIp, printerPort)) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(zplContent.getBytes());
                outputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else if (selectedPrinter != null) {
            try {
                DocPrintJob dpj = selectedPrinter.createPrintJob();
                InputStream stream = new ByteArrayInputStream(zplContent.getBytes());
                DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                Doc doc = new SimpleDoc(stream, flavor, null);
                dpj.print(doc, null);
                return true;
            } catch (PrintException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Nenhuma impressora Zebra foi encontrada.");
            return false;
        }
    }

    public PrintService getSelectedPrinter() {
        return selectedPrinter;
    }

    public String getPrinterIp() {
        return printerIp;
    }
}
