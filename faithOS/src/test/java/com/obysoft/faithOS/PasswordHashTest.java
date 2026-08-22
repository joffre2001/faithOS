package com.obysoft.faithOS;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    public static void main(String[] args) {
        System.out.println(
            new BCryptPasswordEncoder().encode("Synthetic-Test-Password-Only-2026!")
        );
    }
}
