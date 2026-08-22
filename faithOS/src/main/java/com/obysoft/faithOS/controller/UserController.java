package com.obysoft.faithOS.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.UserRequest;
import com.obysoft.faithOS.dto.UserResponse;
import com.obysoft.faithOS.dto.UserStatusRequest;
import com.obysoft.faithOS.dto.UserUpdateRequest;
import com.obysoft.faithOS.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll() {

        return ResponseEntity.ok(
                userService.findAll()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        return ResponseEntity.ok(
                userService.getCurrentUser()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        return ResponseEntity.ok(
                userService.updateUser(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusRequest request) {

        return ResponseEntity.ok(
                userService.updateUserStatus(
                        id,
                        request.getActive()
                )
        );
    }
}
