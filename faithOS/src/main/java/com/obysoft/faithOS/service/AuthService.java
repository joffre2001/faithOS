package com.obysoft.faithOS.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.obysoft.faithOS.dto.LoginRequest;
import com.obysoft.faithOS.dto.LoginResponse;
import com.obysoft.faithOS.exception.InvalidCredentialsException;
import com.obysoft.faithOS.repository.UserRepository;
import com.obysoft.faithOS.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()
                        -> new InvalidCredentialsException(
                        "Invalid email or password."
                )
                );
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );

        }

        LoginResponse response = toResponse(user);
        response.setMessage("Login successful.");
        response.setToken(jwtService.generateToken(user));
        return response;
    }

    public LoginResponse session(String email) {
        var user = userRepository.findByEmail(email)
                .filter(candidate -> Boolean.TRUE.equals(candidate.getActive()))
                .orElseThrow(() -> new InvalidCredentialsException("Session is no longer valid."));
        LoginResponse response = toResponse(user);
        response.setMessage("Session active.");
        return response;
    }

    private LoginResponse toResponse(com.obysoft.faithOS.entity.User user) {
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setFullName(user.getFirstName() + " " + user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setMustChangePassword(user.getMustChangePassword());
        if (user.getChurch() != null) {
            response.setChurchId(user.getChurch().getId());
            response.setChurchName(user.getChurch().getName());
        }
        return response;
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Authenticated user not found."));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
}
