package com.novasoftware.tools.domain.service;

import com.novasoftware.tools.domain.model.ZplFile;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ZplFileService {

    public boolean validateZplContent(String zplContent) {
        return zplContent != null && !zplContent.isEmpty();
    }

    public void saveZplToFile(String zplContent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZPL Files", "*.zpl"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(zplContent);
            }
        }
    }
}