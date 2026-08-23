package com.obysoft.faithOS.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.obysoft.faithOS.dto.SupportRequest;
import com.obysoft.faithOS.entity.User;

@Service
public class SupportService {
    private final JavaMailSender mail;
    private final CurrentChurchService current;
    private final String from;
    private final String recipient;
    private final String smtpHost;

    public SupportService(JavaMailSender mail, CurrentChurchService current,
            @Value("${app.mail.from:no-reply@faithos.app}") String from,
            @Value("${app.support.email:obensonjoffre3@gmail.com}") String recipient,
            @Value("${spring.mail.host:}") String smtpHost) {
        this.mail = mail; this.current = current; this.from = from; this.recipient = recipient; this.smtpHost = smtpHost;
    }

    public void submit(SupportRequest request) {
        if (smtpHost.isBlank()) throw new IllegalStateException("Email delivery is not configured. Ask an administrator to configure SMTP.");
        User user = current.user();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setReplyTo(user.getEmail());
        message.setSubject("FaithOS support — " + request.page().trim());
        message.setText("Submitted by: " + user.getFirstName() + " " + user.getLastName() + " <" + user.getEmail() + ">\n"
                + "Church: " + (user.getChurch() == null ? "Not assigned" : user.getChurch().getName()) + "\n\n"
                + "Expected:\n" + request.expected().trim() + "\n\nWhat happened:\n" + request.error().trim());
        mail.send(message);
    }
}
