package com.novasoftware.tools.domain.service;

import java.util.List;
import java.util.Map;

public class ZplFormatService {

    public String generateZpl(List<Map<String, Object>> eansAndSkus, String labelFormat, String labelType) {
        switch (labelFormat) {
            case "2-Colunas":
                return generateZpl2Columns(eansAndSkus, labelType);
            case "1-Coluna":
                return generateZpl1Column(eansAndSkus, labelType);
            case "4-etiquetas por página":
                return generateZpl4LabelsPerPage(eansAndSkus, labelType);
            default:
                throw new IllegalArgumentException("Formato de etiqueta desconhecido.");
        }
    }

    private String generateZpl2Columns(List<Map<String, Object>> eansAndSkus, String labelType) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int xOffset = 0;
        int yOffset = 0;
        int columnCount = 0;
        int labelHeight = 100;
        int columns = 2;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(generateLabelContent(labelType, xOffset, yOffset, ean, sku));
                columnCount++;
                xOffset += 200;

                if (columnCount == columns) {
                    xOffset = 0;
                    yOffset += labelHeight;
                    columnCount = 0;
                }
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateZpl1Column(List<Map<String, Object>> eansAndSkus, String labelType) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int yOffset = 0;
        int labelHeight = 100;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(generateLabelContent(labelType, 0, yOffset, ean, sku));
                yOffset += labelHeight;
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateZpl4LabelsPerPage(List<Map<String, Object>> eansAndSkus, String labelType) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int xOffset = 0;
        int yOffset = 0;
        int columnCount = 0;
        int rowCount = 0;
        int labelHeight = 100;
        int columns = 2;
        int rows = 2;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(generateLabelContent(labelType, xOffset, yOffset, ean, sku));
                columnCount++;
                xOffset += 200; // Assuming a fixed width for each label

                if (columnCount == columns) {
                    xOffset = 0;
                    yOffset += labelHeight;
                    columnCount = 0;
                    rowCount++;
                }

                if (rowCount == rows) {
                    xOffset = 0;
                    yOffset = 0;
                    rowCount = 0;
                }
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateLabelContent(String labelType, int xOffset, int yOffset, String ean, String sku) {
        switch (labelType) {
            case "Code 128":
                return String.format(
                    "^LH%d,%d\n^FO65,18^BY2,,0^BCN,54,N,N^FD%s^FS\n^FO145,80^A0N,20,25^FH^FD%s^FS\n^FO146,80^A0N,20,25^FH^FD%s^FS\n",
                    xOffset, yOffset, sku, sku, sku
                );
            case "EAN-13":
                return String.format(
                    "^LH%d,%d\n^FO65,18^BY2,,0^BEN,54,N,N^FD%s^FS\n^FO145,80^A0N,20,25^FH^FD%s^FS\n^FO146,80^A0N,20,25^FH^FD%s^FS\n",
                    xOffset, yOffset, ean
                );
            case "QRCode":
                return String.format(
                    "^LH%d,%d\n^FO50,50^BQN,2,10^FDQA,%s^FS\n", xOffset, yOffset, ean
                );
            default:
                throw new IllegalArgumentException("Tipo de etiqueta desconhecido.");
        }
    }
}
