package com.novasoftware.tools.domain.service;

import com.novasoftware.tools.domain.model.ZplFile;

public class ZplFileService {

    public boolean validateZplContent(String zplContent) {
        return zplContent != null && !zplContent.trim().isEmpty();
    }

    public void convertZplToPdf(ZplFile zplFile) {
    }

    public void manageZplFile(ZplFile zplFile) {
    }
}