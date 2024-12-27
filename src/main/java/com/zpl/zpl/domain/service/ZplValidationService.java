package com.zpl.zpl.domain.service;

import java.util.List;
import java.util.Map;

public class ZplValidationService {

    public void validateData(List<Map<String, Object>> eansAndSkus) {
        for (Map<String, Object> item : eansAndSkus) {
            if (!item.containsKey("EAN") || !item.containsKey("SKU") || !item.containsKey("Quantidade")) {
                throw new IllegalArgumentException("Dados inválidos: EAN, SKU e Quantidade são obrigatórios.");
            }
            if (!item.get("EAN").toString().matches("\\d+")) {
                throw new IllegalArgumentException("EAN deve conter apenas números.");
            }
            try {
                Integer.parseInt(item.get("Quantidade").toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Quantidade deve ser um número inteiro.");
            }
        }
    }
}
