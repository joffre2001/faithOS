package com.obysoft.faithOS.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.obysoft.faithOS.dto.ChurchRequest;
import com.obysoft.faithOS.dto.ChurchResponse;
import com.obysoft.faithOS.service.ChurchService;
import com.obysoft.faithOS.service.CurrentChurchService;
import com.obysoft.faithOS.service.MediaImageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/churches")
public class ChurchController {

    private final ChurchService churchService;
    private final CurrentChurchService currentChurchService;
    private final MediaImageService mediaImageService;

    public ChurchController(ChurchService churchService, CurrentChurchService currentChurchService,
            MediaImageService mediaImageService) {
        this.churchService = churchService;
        this.currentChurchService = currentChurchService;
        this.mediaImageService = mediaImageService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ChurchResponse getChurchById(@PathVariable Long id) {
        return churchService.getChurchById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ChurchResponse>> getAllChurches() {
        return ResponseEntity.ok(churchService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ChurchResponse updateChurch(
            @PathVariable Long id,
            @Valid @RequestBody ChurchRequest request) {

        return churchService.updateChurch(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChurch(@PathVariable Long id) {
        churchService.deleteChurch(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ChurchResponse> createChurch(
            @Valid @RequestBody ChurchRequest request) {

        ChurchResponse response = churchService.createChurch(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/current")
    public ChurchResponse currentChurch() {
        return churchService.getChurchById(currentChurchService.church().getId());
    }

    @PutMapping("/current")
    @PreAuthorize("hasRole('CHURCH_ADMIN')")
    public ChurchResponse updateCurrentChurch(@Valid @RequestBody ChurchRequest request) {
        return churchService.updateChurch(currentChurchService.church().getId(), request);
    }

    @PostMapping(value = "/current/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadCurrentChurchLogo(@RequestParam("file") MultipartFile file) {
        mediaImageService.saveChurchLogo(file);
    }

    @GetMapping("/current/logo")
    public ResponseEntity<org.springframework.core.io.Resource> currentChurchLogo() {
        var image = mediaImageService.churchLogo();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=300")
                .header("X-Content-Type-Options", "nosniff")
                .body(image.resource());
    }

    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadChurchLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        mediaImageService.saveChurchLogo(id, file);
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<org.springframework.core.io.Resource> churchLogo(@PathVariable Long id) {
        var image = mediaImageService.churchLogo(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=300")
                .header("X-Content-Type-Options", "nosniff")
                .body(image.resource());
    }
}
