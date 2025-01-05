package com.novasoftware.shared.util.log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordLogger {
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1264392125949280359/-b_spzL_6Q1ZNIedNpuhlWGy-SQqEWAtOq-zQzNBO0xprcG2hdtcwRJnQCu7quVDTHGc";

    public static final String COLOR_RED = "#FF0000";

    public static final String COLOR_GREEN = "#009A31";

    public static void sendLogToDiscord(String title, String message, String description, Class<?> classRegister, String color) {
        try {

            String payload = "{ "
                    + "\"content\": \"" + message + "\", "
                    + "\"embeds\": [{"
                    + "\"title\": \"" + title + "\", "
                    + "\"description\": \"" + description + "\", "
                    + "\"classe\": \"" + classRegister + "\", "
                    + "\"color\": " + Integer.parseInt(color.replace("#", ""), 16)
                    + "}] "
                    + "}";

            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Log enviado para o Discord com sucesso!");
            } else {
                System.out.println("Falha ao enviar log para o Discord. Código de resposta: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Erro ao enviar log para o Discord: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

