package com.novasoftware.core.example;

import java.util.List;
import java.util.Map;

public class ZPLGenerateExample {

    /**
     * Gera um exemplo de ZPL para etiquetas com QR Code.
     *
     * @param data O conteúdo do QR Code.
     * @return O ZPL gerado.
     */
    public static String generateQRCode(String data) {
        return "^XA\n" +
                "^FO50,50\n" +
                "^BQN,2,10\n" +
                "^FDQA," + data + "^FS\n" +
                "^XZ";
    }

    /**
     * Gera um exemplo de ZPL para etiquetas com código de barras Code 128.
     *
     * @param data O conteúdo do código de barras.
     * @return O ZPL gerado.
     */
    public static String generateCode128(String data) {
        return "^XA\n" +
                "^FO50,50\n" +
                "^BCN,100,Y,N,N\n" +
                "^FD" + data + "^FS\n" +
                "^XZ";
    }

    /**
     * Gera um exemplo de ZPL para etiquetas com código de barras EAN-13.
     *
     * @param data O conteúdo do código de barras.
     * @return O ZPL gerado.
     */
    public static String generateEAN13(String data) {
        return "^XA\n" +
                "^FO50,50\n" +
                "^BEN,100,Y,N\n" +
                "^FD" + data + "^FS\n" +
                "^XZ";
    }

    /**
     * Gera um exemplo de ZPL para etiquetas com texto simples.
     *
     * @param text O texto a ser exibido na etiqueta.
     * @return O ZPL gerado.
     */
    public static String generateTextLabel(String text) {
        return "^XA\n" +
                "^FO50,50\n" +
                "^A0N,50,50\n" +
                "^FD" + text + "^FS\n" +
                "^XZ";
    }

    /**
     * Gera um ZPL para etiquetas personalizadas com base em uma lista de dados (exemplo: EAN e SKU).
     *
     * @param eansAndSkus A lista de mapas contendo os dados (chaves: "EAN", "SKU", "Quantidade").
     * @return O ZPL gerado.
     */
    public static String generateCustomLabel(List<Map<String, Object>> eansAndSkus) {
        StringBuilder zpl = new StringBuilder("^XA\n");
        int yPosition = 50;

        for (Map<String, Object> item : eansAndSkus) {
            String sku = (String) item.get("SKU");
            String ean = (String) item.get("EAN");
            int quantity = (int) item.get("Quantidade");

            zpl.append("^FO50,").append(yPosition).append("\n")
                    .append("^A0N,30,30\n")
                    .append("^FD SKU: ").append(sku).append("^FS\n");

            yPosition += 40;

            zpl.append("^FO50,").append(yPosition).append("\n")
                    .append("^A0N,30,30\n")
                    .append("^FD EAN: ").append(ean).append("^FS\n");

            yPosition += 40;

            zpl.append("^FO50,").append(yPosition).append("\n")
                    .append("^A0N,30,30\n")
                    .append("^FD Quantidade: ").append(quantity).append("^FS\n");

            yPosition += 60; // Espaço entre etiquetas
        }

        zpl.append("^XZ");
        return zpl.toString();
    }
}

