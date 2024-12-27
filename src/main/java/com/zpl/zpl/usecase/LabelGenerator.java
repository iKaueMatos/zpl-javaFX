package com.zpl.zpl.usecase;

import java.util.List;
import java.util.Map;

public class LabelGenerator {
    public String generateZpl(List<Map<String, Object>> eansAndSkus, String labelFormat) {
        switch (labelFormat) {
            case "2-Colunas":
                return generateZpl2Columns(eansAndSkus);
            case "1-Coluna":
                return generateZpl1Column(eansAndSkus);
            case "4-etiquetas por página":
                return generateZpl4LabelsPerPage(eansAndSkus);
            case "Entiqueta Envio personalizado":
                return generateZplCustomShipping(eansAndSkus);
            case "QRCode":
                return generateZplQrcode(eansAndSkus);
            case "Code128":
                return generateZplCode128(eansAndSkus);
            default:
                throw new IllegalArgumentException("Formato de etiqueta desconhecido.");
        }
    }

    private String generateZpl2Columns(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int xOffset = 0;
        int yOffset = 0;
        int labelWidth = 400;
        int labelHeight = 150;
        int maxColumns = 2;
        int columnCount = 0;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(String.format(
                    "^LH%d,%d\n^FO65,10^BY3,,60^BEN,60,Y,N^FD%s^FS\n^FO20,105^A0N,20,20^FDSKU: %s^FS\n",
                    xOffset, yOffset, ean, sku
                ));
                columnCount++;
                xOffset += labelWidth;

                if (columnCount == maxColumns) {
                    xOffset = 0;
                    yOffset += labelHeight;
                    columnCount = 0;
                }
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateZpl1Column(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int yOffset = 0;
        int labelHeight = 150;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(String.format(
                    "^LH0,%d\n^FO65,10^BY3,,60^BEN,60,Y,N^FD%s^FS\n^FO20,105^A0N,20,20^FDSKU: %s^FS\n",
                    yOffset, ean, sku
                ));
                yOffset += labelHeight;
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateZpl4LabelsPerPage(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int xOffset = 0;
        int yOffset = 0;
        int labelWidth = 400;
        int labelHeight = 150;
        int maxColumns = 2;
        int maxRows = 2;
        int columnCount = 0;
        int rowCount = 0;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(String.format(
                    "^LH%d,%d\n^FO65,10^BY3,,60^BEN,60,Y,N^FD%s^FS\n^FO20,105^A0N,20,20^FDSKU: %s^FS\n",
                    xOffset, yOffset, ean, sku
                ));
                columnCount++;
                xOffset += labelWidth;

                if (columnCount == maxColumns) {
                    xOffset = 0;
                    yOffset += labelHeight;
                    columnCount = 0;
                    rowCount++;
                }

                if (rowCount == maxRows) {
                    xOffset = 0;
                    yOffset = 0;
                    rowCount = 0;
                }
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateZplCustomShipping(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int xOffset = 0;
        int yOffset = 0;
        int labelWidth = 600;
        int labelHeight = 300;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                if (!ean.matches("\\d+")) {
                    throw new IllegalArgumentException("EAN deve conter apenas números.");
                }
                zpl.append(String.format(
                    "^LH%d,%d\n^FO50,50^GB%d,%d,2^FS\n^FO100,100^BY3,,60^BEN,60,Y,N^FD%s^FS\n",
                    xOffset, yOffset, labelWidth - 100, labelHeight - 100, ean
                ));
                if (sku != null && !sku.isEmpty()) {
                    zpl.append(String.format(
                        "^FO100,200^A0N,30,30^FDSKU: %s^FS\n", sku
                    ));
                }
                zpl.append(String.format(
                    "^FO100,250^A0N,30,30^FDQuantidade: %d^FS\n", quantity
                ));
                yOffset += labelHeight;
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateZplQrcode(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int yOffset = 0;
        int labelHeight = 150;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(String.format(
                    "^LH0,%d\n^FO50,50^BQN,2,10^FDQA,%s^FS\n", yOffset, ean
                ));
                yOffset += labelHeight;
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }

    private String generateZplCode128(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder("^XA^CI28\n");
        int yOffset = 0;
        int labelHeight = 150;

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN").toString();
            String sku = item.get("SKU").toString();
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            for (int i = 0; i < quantity; i++) {
                zpl.append(String.format(
                    "^LH0,%d\n^FO50,50^BCN,100,Y,N,N^FD%s^FS\n", yOffset, ean
                ));
                if (sku != null && !sku.isEmpty()) {
                    zpl.append(String.format(
                        "^FO50,160^A0N,30,30^FDSKU: %s^FS\n", sku
                    ));
                }
                yOffset += labelHeight;
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }
}
