package com.novasoftware.core.http.client;

import com.novasoftware.core.Enum.LabelFormat;
import com.novasoftware.tools.domain.Enum.LabelType;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class LabelaryClient {

    private static final String BASE_URL = "http://api.labelary.com/v1/printers";

    /**
     * Envia o ZPL para a API do Labelary e retorna a resposta como um array de bytes.
     *
     * @param zpl              O conteúdo ZPL a ser enviado.
     * @param printerDensity   A densidade da impressora (por exemplo, 8dpmm).
     * @param labelDimensions  As dimensões do rótulo (por exemplo, "4x6").
     * @param labelIndex       O índice do rótulo na folha.
     * @param outputFormat     O formato de saída (PDF ou PNG).
     * @return A resposta da API como um array de bytes.
     * @throws IOException          Se ocorrer um erro de I/O.
     * @throws InterruptedException Se a requisição for interrompida.
     */
    public static byte[] sendZplToLabelary(String zpl, String printerDensity, String labelDimensions, String labelIndex, String outputFormat) throws IOException, InterruptedException {
        if (zpl == null || zpl.isEmpty()) {
            throw new IllegalArgumentException("O conteúdo ZPL não pode ser nulo ou vazio.");
        }
        if (printerDensity == null || labelDimensions == null || labelIndex == null || outputFormat == null) {
            throw new IllegalArgumentException("Os parâmetros de formato não podem ser nulos.");
        }

        String uriString = String.format("%s/%s/labels/%s/%s/", BASE_URL, printerDensity, labelDimensions, labelIndex);
        URI uri = URI.create(uriString);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .headers("Accept", outputFormat)
                .POST(HttpRequest.BodyPublishers.ofString(zpl))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            String errorMessage = new String(response.body(), StandardCharsets.UTF_8);
            throw new IOException("Erro na API do Labelary: " + response.statusCode() + " - " + errorMessage);
        }
    }

    /**
     * Salva a resposta recebida em um arquivo no sistema de arquivos.
     *
     * @param data     Os dados a serem salvos.
     * @param fileName O nome do arquivo de saída.
     * @throws IOException Se ocorrer um erro ao salvar o arquivo.
     */
    public static void saveResponseToFile(byte[] data, String fileName) throws IOException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Os dados não podem ser nulos ou vazios.");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("O nome do arquivo não pode ser nulo ou vazio.");
        }

        File file = new File(fileName);
        Files.write(file.toPath(), data);
    }
}
