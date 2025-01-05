package com.novasoftware.user.infra.email.strategy;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

public interface EmailTemplateStrategy {
    EmailTemplateData generateEmailContent(Map<String, Object> data) throws TemplateException, IOException;
    String getSubject();
}
