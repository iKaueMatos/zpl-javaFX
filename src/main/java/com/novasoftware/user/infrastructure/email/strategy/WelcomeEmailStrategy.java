package com.novasoftware.user.infrastructure.email.strategy;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class WelcomeEmailStrategy implements EmailTemplateStrategy {
    private final Configuration configuration;

    public WelcomeEmailStrategy() {
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
        return "Seja bem vindo!";
    }

    @Override
    public EmailTemplateData generateEmailContent(Map<String, Object> data) {
        try {
            String templateName = "welcome.html";
            Template template = configuration.getTemplate(templateName);

            StringWriter stringWriter = new StringWriter();
            template.process(data, stringWriter);
            String htmlContent = stringWriter.toString();

            Map<String, Object> templateData = Map.of("htmlContent", htmlContent);
            return new EmailTemplateData(templateName, templateData);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Erro ao gerar e-mail de seja bem vindo", e);
        }
    }
}
