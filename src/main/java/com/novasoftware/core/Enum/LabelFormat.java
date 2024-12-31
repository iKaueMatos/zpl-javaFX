package com.novasoftware.core.Enum;

public enum LabelFormat {
    BASE_URL("http://api.labelary.com/v1/printers"),

    // Configurações de densidade da impressora
    PRINTER_DENSITY_8DPMM("8dpmm"),
    PRINTER_DENSITY_12DPMM("12dpmm"),
    PRINTER_DENSITY_24DPMM("24dpmm"),

    // Dimensões da etiqueta (Largura x Altura)
    LABEL_DIMENSIONS_4X6("4x6"),        // 4 polegadas x 6 polegadas
    LABEL_DIMENSIONS_4X25("4x25"),     // 4 polegadas x 25 polegadas
    LABEL_DIMENSIONS_2X1("2x1"),       // 2 polegadas x 1 polegada
    LABEL_DIMENSIONS_3X2("3x2"),       // 3 polegadas x 2 polegadas
    LABEL_DIMENSIONS_6X4("6x4"),       // 6 polegadas x 4 polegadas
    LABEL_DIMENSIONS_8X12("8x12"),     // 8 polegadas x 12 polegadas
    LABEL_DIMENSIONS_2X8("2x8"),       // 2 polegadas x 8 polegadas
    LABEL_DIMENSIONS_1X1("1x1"),       // 1 polegada x 1 polegada (exemplo de etiqueta pequena)

    // Índice da etiqueta
    LABEL_INDEX_0("0"), // Primeira etiqueta
    LABEL_INDEX_1("1"), // Segunda etiqueta
    LABEL_INDEX_2("2"), // Terceira etiqueta

    // Formatos de saída
    OUTPUT_FORMAT_PDF("application/pdf"), // Formato PDF
    OUTPUT_FORMAT_IMAGE("image/png");    // Formato de imagem PNG

    private final String value;

    LabelFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
