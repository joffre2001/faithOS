package com.obysoft.faithOS.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class SuperAdminBootstrapService implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(SuperAdminBootstrapService.class);
    private final UserRepository users;
    private final String bootstrapEmail;

    public SuperAdminBootstrapService(UserRepository users,
            @Value("${app.bootstrap.super-admin-email:}") String bootstrapEmail) {
        this.users = users;
        this.bootstrapEmail = bootstrapEmail == null ? "" : bootstrapEmail.trim().toLowerCase();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (bootstrapEmail.isBlank() || users.existsByRole(Role.SUPER_ADMIN)) return;

        var user = users.findByEmailIgnoreCase(bootstrapEmail)
                .orElseThrow(() -> new IllegalStateException(
                        "The configured initial super-administrator account does not exist."));
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalStateException(
                    "The configured initial super-administrator account is inactive.");
        }

        user.setRole(Role.SUPER_ADMIN);
        users.save(user);
        log.info("Initial super administrator assigned successfully.");
    }
}
