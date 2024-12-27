package com.zpl.zpl.application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ZplFileGenerator {
    public String generateZplContent(String input) {
        String zplContent = "^XA\n" + 
                            "^FO50,50\n" + 
                            "^ADN,36,20\n" + 
                            "^FD" + input + "^FS\n" + 
                            "^XZ";
        return zplContent;
    }

    public void saveZplFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        }
    }
}