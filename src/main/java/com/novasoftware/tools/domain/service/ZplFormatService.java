package com.novasoftware.tools.domain.service;

import java.util.List;
import java.util.Map;

public class ZplFormatService {

    public String generateZpl(String labelFormat, List<Map<String, Object>> eansAndSkus, String labelType) {
        switch (labelFormat) {
            case "1-Coluna":
                return generateZpl1Column(eansAndSkus, labelType);
            case "2-Colunas":
                return generateZpl2Columns(eansAndSkus, labelType);
            case "4-etiquetas por página":
                return generateZpl4LabelsPerPage(eansAndSkus, labelType);
            default:
                throw new IllegalArgumentException("Formato de etiqueta desconhecido.");
        }
    }

    private String generateZpl2Columns(List<Map<String, Object>> eansAndSkus, String labelType) {
        StringBuilder zpl = new StringBuilder();

        for (Map<String, Object> item : eansAndSkus) {
            String ean1 = item.get("EAN") != null ? item.get("EAN").toString() : null;
            String sku1 = item.get("SKU") != null ? item.get("SKU").toString() : null;
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA^CI28\n");

                if (ean1 != null && !ean1.isEmpty()) {
                    zpl.append(String.format(
                            "^FO65,18^BY2,,0^BCN,54,N,N^%s^FS\n" +
                                    "^FO145,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FO146,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FS\n" +
                                    "^CI28\n" +
                                    "^LH0,0\n" +
                                    "^FO475,18^BY2,,0^BCN,54,N,N^%s^FS\n" +
                                    "^FO555,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FO556,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FS\n" +
                                    "^XZ",
                            ean1, ean1, ean1, ean1, ean1, ean1
                    ));
                }

                if (sku1 != null && !sku1.isEmpty()) {
                    zpl.append(String.format(
                            "^FO65,18^BY2,,0^BCN,54,N,N^%s^FS\n" +
                                    "^FO145,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FO146,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FS\n" +
                                    "^CI28\n" +
                                    "^LH0,0\n" +
                                    "^FO475,18^BY2,,0^BCN,54,N,N^%s^FS\n" +
                                    "^FO555,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FO556,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FS\n" +
                                    "^XZ",
                            sku1, sku1, sku1, sku1, sku1, sku1
                    ));
                }
            }
        }

        return zpl.toString();
    }

    private String generateZpl1Column(List<Map<String, Object>> eansAndSkus, String labelType) {
        StringBuilder zpl = new StringBuilder();

        for (Map<String, Object> item : eansAndSkus) {
            String ean1 = item.get("EAN") != null ? item.get("EAN").toString() : null;
            String sku1 = item.get("SKU") != null ? item.get("SKU").toString() : null;
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA^CI28\n");

                if (ean1 != null && !ean1.isEmpty()) {
                    zpl.append(String.format(
                            "^FO65,18^BY2,,0^BCN,54,N,N^%s^FS\n" +
                                    "^FO145,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FO146,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FS\n" +
                                    "^CI28\n" +
                                    "^XZ",
                            ean1, ean1, ean1
                    ));
                }

                if (sku1 != null && !sku1.isEmpty()) {
                    zpl.append(String.format(
                            "^FO65,18^BY2,,0^BCN,54,N,N^%s^FS\n" +
                                    "^FO145,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FO146,80^A0N,20,25^FH^%s^FS\n" +
                                    "^FS\n" +
                                    "^CI28\n" +
                                    "^XZ",
                            sku1, sku1, sku1
                    ));
                }
            }
        }

        return zpl.toString();
    }

    private String generateZpl4LabelsPerPage(List<Map<String, Object>> data, String labelType) {
        StringBuilder zpl = new StringBuilder();

        for (Map<String, Object> item : data) {
            int quantity = Integer.parseInt(item.get("Quantidade").toString());
            String content = item.get("Dados").toString();
            for (int i = 0; i < quantity; i++) {
                zpl.append(String.format("^XA\n" +
                        "^FO50,50\n" +
                        "^BQN,2,10\n" +
                        "^FDMA,%s^FS\n" +
                        "\n" +
                        "^FO400,50\n" +
                        "^BQN,2,10\n" +
                        "^FDMA,%s^FS\n" +
                        "\n" +
                        "^FO50,400\n" +
                        "^BQN,2,10\n" +
                        "^FDMA,%s^FS\n" +
                        "\n" +
                        "^FO400,400\n" +
                        "^BQN,2,10\n" +
                        "^FDMA,%s^FS\n" +
                        "\n" +
                        "^XZ\n", content));
            }
        }

        zpl.append("^XZ");
        return zpl.toString();
    }
}
