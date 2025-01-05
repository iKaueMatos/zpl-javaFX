package com.novasoftware.user.infra.email.strategy;

import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class PasswordResetEmailSucessStrategy implements EmailTemplateStrategy {
     private final Configuration configuration;

    public PasswordResetEmailSucessStrategy() {
        this.configuration = new Configuration(Configuration.VERSION_2_3_31);
        this.configuration.setClassForTemplateLoading(this.getClass(), "/templates/");
        this.configuration.setDefaultEncoding("UTF-8");
    }

    @Override
    public String getSubject() {
        return "Senha Alterada com sucesso!";
    }

    @Override
    public EmailTemplateData generateEmailContent(Map<String, Object> data) {
        try {
            String templateType = "password-redefine-password-success-email.html";
            Template template = configuration.getTemplate(templateType);
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);

            Map<String, Object> templateData = Map.of("htmlContent", htmlContent);
            return new EmailTemplateData(templateType, templateData);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar e-mail de senha alterada com sucesso!", e);
        }
    }
}
