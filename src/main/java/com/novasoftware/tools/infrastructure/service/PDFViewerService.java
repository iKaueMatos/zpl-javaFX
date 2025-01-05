package com.novasoftware.tools.infrastructure.service;

import com.novasoftware.core.http.client.LabelaryClient;

import java.io.IOException;

public class PDFViewerService {
    public byte[] pdfTag(String zpl, String printerDensity, String labelDimensions, String labelIndex, String outputFormat) {
        try {
            return LabelaryClient.sendZplToLabelary(zpl, printerDensity, labelDimensions, labelIndex, outputFormat);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar o PDF: " + e.getMessage(), e);
        }
    }
}
