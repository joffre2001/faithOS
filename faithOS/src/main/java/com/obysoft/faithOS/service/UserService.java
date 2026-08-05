package com.obysoft.faithOS.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.obysoft.faithOS.dto.UserRequest;
import com.obysoft.faithOS.dto.UserResponse;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.DuplicateResourceException;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChurchRepository churchRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            ChurchRepository churchRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.churchRepository = churchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists.");
        }

        Church church = churchRepository.findById(request.getChurchId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Church not found with id: " + request.getChurchId()
                )
                );

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setChurch(church);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public List<UserResponse> findAll() {

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        String authenticatedEmail = authentication.getName();

        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Authenticated user not found.")
                );

        List<User> users;

        if (authenticatedUser.getRole() == Role.SUPER_ADMIN) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findAllByChurchId(
                    authenticatedUser.getChurch().getId()
            );
        }

        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setActive(user.getActive());

        if (user.getChurch() != null) {
            response.setChurchName(user.getChurch().getName());
        }

        return response;
    }

    public UserResponse getCurrentUser() {

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Authenticated user not found."
                )
                );

        return toResponse(user);
    }
}
