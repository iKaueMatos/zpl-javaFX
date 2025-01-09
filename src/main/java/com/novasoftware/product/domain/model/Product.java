package com.novasoftware.product.domain.model;

import com.novasoftware.Supplier.domain.model.Supplier;
import com.novasoftware.category.domain.model.Category;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String sku;
    private String variationSku;
    private String ean;
    private Integer quantity;
    private Double salePrice;
    private Category category;
    private Date expiryDate;
    private Supplier supplier;
    private Double weight;
    private List<String> images;
    private Date creationDate;
    private Date lastUpdatedDate;
    private String error;
    
    public Product() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getVariationSku() {
        return variationSku;
    }

    public void setVariationSku(String variationSku) {
        this.variationSku = variationSku;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null && !error.isEmpty();
    }

    @Override
    public String toString() {
        return "ProductData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", variationSku='" + variationSku + '\'' +
                ", ean='" + ean + '\'' +
                ", quantity=" + quantity +
                ", salePrice=" + salePrice +
                ", category=" + category +
                ", expiryDate=" + expiryDate +
                ", supplier=" + supplier +
                ", weight=" + weight +
                ", images=" + images +
                ", creationDate=" + creationDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", error='" + error + '\'' +
                '}';
    }
}
