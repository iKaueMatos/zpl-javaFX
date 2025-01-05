package com.novasoftware.user.infra.email.strategy;

import java.util.Map;

public class EmailTemplateData {
    private String templateType;
    private Map<String, Object> templateData;

    public EmailTemplateData(String templateType, Map<String, Object> templateData) {
        this.templateType = templateType;
        this.templateData = templateData;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
    }
}

