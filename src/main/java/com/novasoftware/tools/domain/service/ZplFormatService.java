package com.novasoftware.tools.domain.service;

import com.novasoftware.tools.domain.Enum.LabelConstants;

import java.util.*;
import java.util.function.Function;

public class ZplFormatService {
    private static final Set<String> TYPEBARCODES = Set.of("BEN", "BCN", "B3N", "BU", "FDMA");

    public String generateZpl(String labelFormat, List<Map<String, Object>> eansAndSkus, String labelType) {
        if (labelType == null || labelType.isEmpty()) {
            throw new IllegalArgumentException("Tipo de etiqueta não pode ser nulo ou vazio.");
        }

        Map<String, Function<List<Map<String, Object>>, String>> labelGenerators = new HashMap<>();
        labelGenerators.put(LabelConstants.LABEL_CODE_128, this::generateZplCode128);
        labelGenerators.put(LabelConstants.LABEL_CODE_39, this::generateZplCode39);
        labelGenerators.put(LabelConstants.LABEL_EAN_13, this::generateZplEAN13);
        labelGenerators.put(LabelConstants.LABEL_UPC_A, this::generateZplUPC_A);
        labelGenerators.put(LabelConstants.LABEL_QR_CODE, this::generateZplQRCode);

        Function<List<Map<String, Object>>, String> generatorFunction = labelGenerators.getOrDefault(labelType, this::generateZplDefault);

        switch (labelFormat) {
            case LabelConstants.FORMAT_1_COLUMN:
                return generateZpl1Column(eansAndSkus, labelType);

            case LabelConstants.FORMAT_2_COLUMNS:
                return generateZpl2Columns(eansAndSkus, labelType);

            case LabelConstants.FORMAT_4_LABELS:
                return generateZpl4LabelsPerPage(eansAndSkus, labelType);

            default:
                throw new IllegalArgumentException("Formato de etiqueta desconhecido.");
        }
    }

    private String generateZpl2Columns(List<Map<String, Object>> eansAndSkus, String labelType) {
        StringBuilder zpl = new StringBuilder();

        for (Map<String, Object> item : eansAndSkus) {
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA^CI28\n");

                String sku = Optional.ofNullable(item.get("SKU")).map(Object::toString).orElse("");
                String ean = Optional.ofNullable(item.get("EAN")).map(Object::toString).orElse("");

                Optional.of(sku.isEmpty() ? ean : sku)
                        .ifPresent(value -> {
                            if (value.equals(ean)) {
                                generateEAN(zpl, ean);
                            } else {
                                generateSKU(zpl, sku);
                            }
                        });

                zpl.append("^XZ\n");
            }
        }

        return zpl.toString();
    }

    private void generateEAN(StringBuilder zpl, String ean) {
        zpl.append("^PW800\n");
        zpl.append("^LL200\n");
        zpl.append("^CI28\n");
        zpl.append("^LH0,0\n");
        zpl.append(String.format("^FO80,35^BY3,80,Y^BEN,100,Y^FD%s^FS\n", ean));
        zpl.append(String.format("^FO475,35^BY3,80,Y^BEN,100,Y^FD%s^FS\n", ean));
    }

    private void generateSKU(StringBuilder zpl, String sku) {
        String typebarcode = "BCN";

        zpl.append(String.format(
                "^LH0,0\n" +
                        "^FO65,18^BY2,,0^%s,54,N,N^FD%s^FS\n" +
                        "^FO145,80^A0N,20,28^FH^FD%s^FS\n" +
                        "^FO146,80^A0N,20,28^FH^FD%s^FS\n" +
                        "^FS\n" +
                        "^CI28\n" +
                        "^LH0,0\n" +
                        "^FO475,18^BY2,,0^%s,54,N,N^FD%s^FS\n" +
                        "^FO555,80^A0N,20,28^FH^FD%s^FS\n" +
                        "^FO556,80^A0N,20,28^FH^FD%s^FS\n" +
                        "^FS\n",
                typebarcode, sku, sku, sku, typebarcode, sku, sku, sku
        ));
    }

    private String generateZpl1Column(List<Map<String, Object>> eansAndSkus, String labelType) {
        StringBuilder zpl = new StringBuilder();

        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN") != null ? item.get("EAN").toString() : null;
            String sku = item.get("SKU") != null ? item.get("SKU").toString() : null;
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA^CI28\n");

                if (ean != null && !ean.isEmpty()) {
                    zpl.append(generateBarcode(ean, labelType));
                }

                if (sku != null && !sku.isEmpty()) {
                    zpl.append(generateBarcode(sku, labelType));
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

        return zpl.toString();
    }

    private String generateBarcode(String data, String labelType) {
        switch (labelType) {
            case LabelConstants.LABEL_CODE_128:
                return String.format("^BY2,,0^BCN,54,N,N^FD%s^FS^XZ", data);
            case LabelConstants.LABEL_CODE_39:
                return String.format("^BY2,,0^B3N,50,Y,N^FD%s^FS", data);
            case LabelConstants.LABEL_EAN_13:
                return String.format("^BY2,,0^BEN,54,Y,N^FD%s^FS", data);
            case LabelConstants.LABEL_UPC_A:
                return String.format("^BY2,,0^BU,54,Y,N^FD%s^FS", data);
            case LabelConstants.LABEL_QR_CODE:
                return String.format("^BQN,2,10^FDMA,%s^FS", data);
            default:
                return "";
        }
    }

    private String generateZplDefault(List<Map<String, Object>> eansAndSkus) {
        return "^XA^FO50,50^GB500,500,500^FS^XZ";
    }

    private String generateZplCode128(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder();
        for (Map<String, Object> item : eansAndSkus) {
            String sku = item.get("SKU") != null ? item.get("SKU").toString() : "";
            String ean = item.get("EAN") != null ? item.get("EAN").toString() : "";
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA\n");

                if (!sku.isEmpty()) {
                    zpl.append(String.format("^BY2,,0^BCN,54,N,N^FD%s^FS\n", sku));
                } else if (!ean.isEmpty()) {
                    zpl.append(String.format("^BY2,,0^BEN,54,Y,N^FD%s^FS\n", ean));
                }

                zpl.append("^XZ\n");
            }
        }
        return zpl.toString();
    }

    private String generateZplCode39(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder();
        for (Map<String, Object> item : eansAndSkus) {
            String sku = item.get("SKU") != null ? item.get("SKU").toString() : "";
            String ean = item.get("EAN") != null ? item.get("EAN").toString() : "";
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA\n");

                if (!sku.isEmpty()) {
                    zpl.append(String.format("^BY2,,0^B3N,50,Y,N^FD%s^FS\n", sku));
                } else if (!ean.isEmpty()) {
                    zpl.append(String.format("^BY2,,0^BEN,54,Y,N^FD%s^FS\n", ean));
                }

                zpl.append("^XZ\n");
            }
        }
        return zpl.toString();
    }

    private String generateZplEAN13(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder();
        for (Map<String, Object> item : eansAndSkus) {
            String ean = item.get("EAN") != null ? item.get("EAN").toString() : "";
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA\n");

                if (!ean.isEmpty()) {
                    zpl.append(String.format("^BY2,,0^BEN,54,Y,N^FD%s^FS\n", ean));
                }

                zpl.append("^XZ\n");
            }
        }
        return zpl.toString();
    }

    private String generateZplUPC_A(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder();
        for (Map<String, Object> item : eansAndSkus) {
            String sku = item.get("SKU") != null ? item.get("SKU").toString() : "";
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA\n");

                if (!sku.isEmpty()) {
                    zpl.append(String.format("^BY2,,0^BU,54,Y,N^FD%s^FS\n", sku));
                }

                zpl.append("^XZ\n");
            }
        }
        return zpl.toString();
    }

    private String generateZplQRCode(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder();
        for (Map<String, Object> item : eansAndSkus) {
            String content = item.get("Dados") != null ? item.get("Dados").toString() : "";
            int quantity = Integer.parseInt(item.get("Quantidade").toString());

            for (int i = 0; i < quantity; i++) {
                zpl.append("^XA\n");

                if (!content.isEmpty()) {
                    zpl.append(String.format("^BQN,2,10^FDMA,%s^FS\n", content));
                }

                zpl.append("^XZ\n");
            }
        }
        return zpl.toString();
    }
}
