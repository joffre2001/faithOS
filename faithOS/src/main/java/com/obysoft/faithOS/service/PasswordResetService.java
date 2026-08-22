package com.obysoft.faithOS.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.entity.PasswordResetToken;
import com.obysoft.faithOS.exception.InvalidCredentialsException;
import com.obysoft.faithOS.repository.PasswordResetTokenRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class PasswordResetService {
    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationContext applicationContext;
    private final String from;
    private final String smtpHost;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository users, PasswordResetTokenRepository tokens,
            PasswordEncoder passwordEncoder, ApplicationContext applicationContext,
            @Value("${app.mail.from:no-reply@faithos.app}") String from,
            @Value("${spring.mail.host:}") String smtpHost) {
        this.users = users; this.tokens = tokens; this.passwordEncoder = passwordEncoder;
        this.applicationContext = applicationContext; this.from = from; this.smtpHost = smtpHost;
    }

    @Transactional
    public void request(String email) {
        users.findByEmail(email.trim().toLowerCase()).filter(user -> Boolean.TRUE.equals(user.getActive()))
                .ifPresent(user -> {
                    tokens.deleteAllByUser(user);
                    String raw = generateToken();
                    PasswordResetToken reset = new PasswordResetToken();
                    reset.setTokenHash(hash(raw)); reset.setUser(user);
                    reset.setExpiresAt(LocalDateTime.now().plusMinutes(30));
                    tokens.save(reset);
                    send(user.getEmail(), raw);
                });
    }

    @Transactional
    public void confirm(String rawToken, String newPassword) {
        PasswordResetToken reset = tokens.findByTokenHash(hash(rawToken.trim()))
                .filter(value -> value.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new InvalidCredentialsException("This reset code is invalid or expired."));
        reset.getUser().setPassword(passwordEncoder.encode(newPassword));
        reset.getUser().setMustChangePassword(false);
        users.save(reset.getUser());
        tokens.deleteAllByUser(reset.getUser());
    }

    private String generateToken() {
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception exception) { throw new IllegalStateException("Unable to secure reset token.", exception); }
    }

    private void send(String recipient, String token) {
        if (smtpHost.isBlank()) return;
        try {
            Class<?> senderType = Class.forName("org.springframework.mail.javamail.JavaMailSender");
            Class<?> messageType = Class.forName("org.springframework.mail.SimpleMailMessage");
            Object sender = applicationContext.getBean(senderType);
            Object message = messageType.getConstructor().newInstance();
            messageType.getMethod("setFrom", String.class).invoke(message, from);
            messageType.getMethod("setTo", String[].class).invoke(message, (Object) new String[]{recipient});
            messageType.getMethod("setSubject", String.class).invoke(message, "Your FaithOS password reset code");
            messageType.getMethod("setText", String.class).invoke(message,
                    "Use this code to reset your FaithOS password:\n\n" + token + "\n\nThis code expires in 30 minutes.");
            Object messages = java.lang.reflect.Array.newInstance(messageType, 1);
            java.lang.reflect.Array.set(messages, 0, message);
            senderType.getMethod("send", messages.getClass()).invoke(sender, messages);
        } catch (ClassNotFoundException exception) {
            // Mail is optional in local development; Maven/Gradle refresh will add it when configured.
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to send the password reset email.", exception);
        }
    }
}
