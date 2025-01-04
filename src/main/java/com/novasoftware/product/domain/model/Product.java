package com.novasoftware.product.domain.model;

public class Product {
    private int id;
    private String name;
    private String sku;
    private String skuVariation;
    private String ean;
    private int quantity;

    public Product(int id, String name, String sku, String skuVariation, String ean, int quantity) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.skuVariation = skuVariation;
        this.ean = ean;
        this.quantity = quantity;
    }

    public Product() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSkuVariation() {
        return skuVariation;
    }

    public void setSkuVariation(String skuVariation) {
        this.skuVariation = skuVariation;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
