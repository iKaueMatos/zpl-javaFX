package com.novasoftware.core.zebra;

public class ZebraPrinterService {
    private final ZebraPrinterConfigurationService zebraPrinterConfiguration;

    public ZebraPrinterService() {
        this.zebraPrinterConfiguration = new ZebraPrinterConfigurationService();
    }

    public void detectZebraPrinters() {
        zebraPrinterConfiguration.detectZebraPrinters();
    }

    public void printLabel(String zplCommand) {
        String selectedPrinterIp = ZebraPrinterConfigurationService.selectPrinter();
        if (selectedPrinterIp != null) {
            ZebraPrinterConfigurationService.printLabel(selectedPrinterIp, zplCommand);
        }
    }
}
