package com.novasoftware.core.http.client;

import com.novasoftware.core.Enum.LabelFormat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class LabelaryClient {

    /**
     * Envia o ZPL para a API do Labelary e retorna a resposta como um array de bytes.
     *
     * @param zpl O conteúdo ZPL a ser enviado.
     * @return A resposta da API como um array de bytes.
     * @throws IOException Se ocorrer um erro de I/O.
     * @throws InterruptedException Se a requisição for interrompida.
     */
    public static byte[] sendZplToLabelary(String zpl, String printerDensity, String labelDimensions, String labelIndex, String outputFormat) throws IOException, InterruptedException {
        URI uri = URI.create(String.format("%s/%s/labels/%s/%s/", LabelFormat.BASE_URL, printerDensity, labelDimensions, labelIndex));
        
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", outputFormat)
                .POST(HttpRequest.BodyPublishers.ofString(zpl))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            String errorMessage = new String(response.body(), StandardCharsets.UTF_8);
            throw new IOException("Erro na API do Labelary: " + errorMessage);
        }
    }

    /**
     * Salva a resposta recebida em um arquivo no sistema de arquivos.
     *
     * @param data Os dados a serem salvos.
     * @param fileName O nome do arquivo de saída.
     * @throws IOException Se ocorrer um erro ao salvar o arquivo.
     */
    public static void saveResponseToFile(byte[] data, String fileName) throws IOException {
        File file = new File(fileName);
        Files.write(file.toPath(), data);
    }
}

