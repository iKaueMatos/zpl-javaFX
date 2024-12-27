package com.zpl.zpl.domain.service;

import com.zpl.zpl.domain.model.ZplFile;

public class ZplFileService {

    public boolean validateZplContent(String zplContent) {
        return zplContent != null && !zplContent.trim().isEmpty();
    }

    public void convertZplToPdf(ZplFile zplFile) {
    }

    public void manageZplFile(ZplFile zplFile) {
    }
}