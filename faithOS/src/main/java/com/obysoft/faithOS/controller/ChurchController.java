package com.obysoft.faithOS.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.obysoft.faithOS.dto.ChurchRequest;
import com.obysoft.faithOS.dto.ChurchResponse;
import com.obysoft.faithOS.service.ChurchService;
import com.obysoft.faithOS.service.CurrentChurchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/churches")
public class ChurchController {

    private final ChurchService churchService;
    private final CurrentChurchService currentChurchService;

    public ChurchController(ChurchService churchService, CurrentChurchService currentChurchService) {
        this.churchService = churchService;
        this.currentChurchService = currentChurchService;
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
}
