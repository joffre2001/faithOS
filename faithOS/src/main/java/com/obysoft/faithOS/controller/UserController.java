package com.obysoft.faithOS.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.obysoft.faithOS.dto.PageResponse;
import com.obysoft.faithOS.dto.UserRequest;
import com.obysoft.faithOS.dto.UserResponse;
import com.obysoft.faithOS.dto.UserStatusRequest;
import com.obysoft.faithOS.dto.UserUpdateRequest;
import com.obysoft.faithOS.service.UserService;
import com.obysoft.faithOS.service.MediaImageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;
    private final MediaImageService mediaImageService;

    public UserController(UserService userService, MediaImageService mediaImageService) {
        this.userService = userService;
        this.mediaImageService = mediaImageService;
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN', 'PASTOR', 'LEADER')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by("firstName").ascending().and(Sort.by("lastName").ascending()));

        return ResponseEntity.ok(PageResponse.from(
                userService.findAll(pageable, search)));
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

    @PostMapping("/{id}/invite")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN')")
    public ResponseEntity<Void> sendInvitation(@PathVariable Long id) {
        userService.sendInvitation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        mediaImageService.saveProfilePicture(id, file);
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<org.springframework.core.io.Resource> profilePicture(@PathVariable Long id) {
        var image = mediaImageService.profilePicture(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=300")
                .header("X-Content-Type-Options", "nosniff")
                .body(image.resource());
    }
}
