package com.novasoftware.user.infra.email.strategy;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class PasswordResetEmailSucessStrategy implements EmailTemplateStrategy {
    private final Configuration configuration;

    public PasswordResetEmailSucessStrategy() {
        this.configuration = new Configuration(Configuration.VERSION_2_3_31);
        try {
            configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
            configuration.setDefaultEncoding("UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao configurar o carregamento do template", e);
        }
    }

    @Override
    public String getSubject() {
        return "Senha alterada com sucesso!";
    }

    @Override
    public EmailTemplateData generateEmailContent(Map<String, Object> data) {
        try {
            String templateName = "password-redefine-success-email.html";
            Template template = configuration.getTemplate(templateName);

            StringWriter stringWriter = new StringWriter();
            template.process(data, stringWriter);
            String htmlContent = stringWriter.toString();

            Map<String, Object> templateData = Map.of("htmlContent", htmlContent);
            return new EmailTemplateData(templateName, templateData);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Erro ao gerar e-mail de senha alterada com sucesso!", e);
        }
    }
}
