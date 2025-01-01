package com.novasoftware.tools.domain.Enum;

public enum LabelType {
    EAN_13("EAN-13 - Código de barras padrão internacional"),
    UPC_A("UPC-A - Código de barras para produtos americanos"),
    CODE_128("Code 128 - Código de barras linear"),
    CODE_39("Code 39 - Código de barras alfanumérico"),
    QR_CODE("QR Code - Código bidimensional");

    private final String label;

    LabelType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static LabelType fromString(String label) {
        for (LabelType type : LabelType.values()) {
            if (type.getLabel().equals(label)) {
                return type;
            }
        }
        return null;
    }
}

