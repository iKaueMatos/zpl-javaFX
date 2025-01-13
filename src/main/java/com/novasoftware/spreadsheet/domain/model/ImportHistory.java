package com.novasoftware.spreadsheet.domain.model;

import java.util.Date;

public class ImportHistory {
    private Long id;
    private Date importDate;
    private String status;
    private Integer importedProductsCount;
    private String errorDetails;
    private Date createdAt;
    private Date updatedAt;

    public ImportHistory() {}

    public ImportHistory(Long id, Date importDate, String status, Integer importedProductsCount,
                         String errorDetails, Date createdAt, Date updatedAt) {
        this.id = id;
        this.importDate = importDate;
        this.status = status;
        this.importedProductsCount = importedProductsCount;
        this.errorDetails = errorDetails;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getImportedProductsCount() {
        return importedProductsCount;
    }

    public void setImportedProductsCount(Integer importedProductsCount) {
        this.importedProductsCount = importedProductsCount;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ImportHistoryService{" +
                "id=" + id +
                ", importDate=" + importDate +
                ", status='" + status + '\'' +
                ", importedProductsCount=" + importedProductsCount +
                ", errorDetails='" + errorDetails + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
