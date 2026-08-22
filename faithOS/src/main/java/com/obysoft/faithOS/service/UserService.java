package com.obysoft.faithOS.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.obysoft.faithOS.dto.UserRequest;
import com.obysoft.faithOS.dto.UserResponse;
import com.obysoft.faithOS.dto.UserUpdateRequest;
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
            throw new DuplicateResourceException(
                    "Email already exists."
            );
        }

        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() == Role.CHURCH_ADMIN
                && !authenticatedUser.getChurch().getId()
                        .equals(request.getChurchId())) {

            throw new AccessDeniedException(
                    "You cannot create users for another church."
            );
        }

        if (authenticatedUser.getRole() != Role.SUPER_ADMIN
                && request.getRole() == Role.SUPER_ADMIN) {

            throw new AccessDeniedException(
                    "Only a super administrator can create a super administrator."
            );
        }

        Church church = churchRepository.findById(request.getChurchId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Church not found with id: "
                        + request.getChurchId()
                )
                );

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setChurch(church);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public List<UserResponse> findAll() {

        User authenticatedUser = getAuthenticatedUser();

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

    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request) {

        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "User not found with id: " + id
                )
                );

        if (authenticatedUser.getRole() == Role.CHURCH_ADMIN
                && !authenticatedUser.getChurch().getId()
                        .equals(user.getChurch().getId())) {

            throw new AccessDeniedException(
                    "You cannot update a user from another church."
            );
        }

        if (authenticatedUser.getRole() != Role.SUPER_ADMIN
                && request.getRole() == Role.SUPER_ADMIN) {

            throw new AccessDeniedException(
                    "Only a super administrator can assign this role."
            );
        }

        if (userRepository.existsByEmailAndIdNot(
                request.getEmail(), id)) {

            throw new DuplicateResourceException(
                    "Email already exists."
            );
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public UserResponse updateUserStatus(
            Long id,
            Boolean active) {

        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "User not found with id: " + id
                )
                );

        if (authenticatedUser.getId().equals(user.getId())
                && Boolean.FALSE.equals(active)) {

            throw new AccessDeniedException(
                    "You cannot deactivate your own account."
            );
        }

        if (authenticatedUser.getRole() == Role.CHURCH_ADMIN
                && !authenticatedUser.getChurch().getId()
                        .equals(user.getChurch().getId())) {

            throw new AccessDeniedException(
                    "You cannot manage a user from another church."
            );
        }

        if (authenticatedUser.getRole() != Role.SUPER_ADMIN
                && user.getRole() == Role.SUPER_ADMIN) {

            throw new AccessDeniedException(
                    "Only a super administrator can manage this account."
            );
        }

        user.setActive(active);

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public UserResponse getCurrentUser() {
        return toResponse(getAuthenticatedUser());
    }

    private User getAuthenticatedUser() {

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Authenticated user not found."
                )
                );
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
            response.setChurchName(
                    user.getChurch().getName()
            );
        }

        return response;
    }
}
