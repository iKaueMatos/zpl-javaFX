package com.zpl.zpl.application.usecase;

import com.zpl.zpl.domain.service.ZplFormatService;
import com.zpl.zpl.domain.service.ZplValidationService;

import java.util.List;
import java.util.Map;

public class LabelGenerator {
    private final ZplFormatService zplFormatService;
    private final ZplValidationService zplValidationService;

    public LabelGenerator() {
        this.zplFormatService = new ZplFormatService();
        this.zplValidationService = new ZplValidationService();
    }

    public String generateZpl(List<Map<String, Object>> eansAndSkus, String labelFormat, String labelType, int labelWidth, int labelHeight, int columns, int rows) {
        zplValidationService.validateData(eansAndSkus);
        return zplFormatService.generateZpl(eansAndSkus, labelFormat, labelType, labelWidth, labelHeight, columns, rows);
    }
}
