package com.novasoftware.tools.application.usecase;

import com.novasoftware.tools.domain.service.ZplValidationService;
import com.novasoftware.tools.domain.service.ZplFormatService;

import java.util.List;
import java.util.Map;

public class LabelGeneratorService {
    private final ZplFormatService zplFormatService;
    private final ZplValidationService zplValidationService;

    public LabelGeneratorService() {
        this.zplFormatService = new ZplFormatService();
        this.zplValidationService = new ZplValidationService();
    }

    public String generateZpl(
            List<Map<String, Object>> eansAndSkus,
            String labelFormat,
            String labelType
    ) {
        return zplFormatService.generateZpl(labelFormat, eansAndSkus, labelType);
    }
}
