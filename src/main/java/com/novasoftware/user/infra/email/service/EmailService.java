package com.novasoftware.user.infra.email.service;

import com.novasoftware.user.infra.email.strategy.EmailTemplateData;
import com.novasoftware.user.infra.email.strategy.EmailTemplateStrategy;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Map;
import java.util.Properties;

public class EmailService {
    private JavaMailSender sender;
    private String senderEmail = "novasoftwareorganization@gmail.com";
    private String senderPassword = "uhmczwarlshtektl";

    public EmailService() {
        this.sender = createJavaMailSender();
    }

    private JavaMailSender createJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(senderEmail);
        mailSender.setPassword(senderPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    public void sendEmail(String recipient, EmailTemplateStrategy strategy, Map<String, Object> data) {
        try {
            if (sender == null) {
                throw new RuntimeException("JavaMailSender não foi inicializado!");
            }

            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            EmailTemplateData templateData = strategy.generateEmailContent(data);

            helper.setSubject(strategy.getSubject());
            helper.setTo(recipient);
            helper.setFrom(senderEmail);
            helper.setText(templateData.getTemplateData().get("htmlContent").toString(), true);

            sender.send(message);
        } catch (AddressException e) {
            throw new RuntimeException("O endereço de e-mail fornecido é inválido: " + recipient, e);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar o e-mail: " + e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Dados faltando para gerar o conteúdo do e-mail.", e);
        } catch (Exception e) {
            throw new RuntimeException("Ocorreu um erro inesperado ao enviar o e-mail.", e);
        }
    }
}
