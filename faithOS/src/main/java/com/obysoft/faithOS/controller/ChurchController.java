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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/churches")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class ChurchController {

    private final ChurchService churchService;

    public ChurchController(ChurchService churchService) {
        this.churchService = churchService;
    }

    @GetMapping("/{id}")
    public ChurchResponse getChurchById(@PathVariable Long id) {
        return churchService.getChurchById(id);
    }

    @GetMapping
    public ResponseEntity<List<ChurchResponse>> getAllChurches() {
        return ResponseEntity.ok(churchService.findAll());
    }

    @PutMapping("/{id}")
    public ChurchResponse updateChurch(
            @PathVariable Long id,
            @Valid @RequestBody ChurchRequest request) {

        return churchService.updateChurch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChurch(@PathVariable Long id) {
        churchService.deleteChurch(id);
    }

    @PostMapping
    public ResponseEntity<ChurchResponse> createChurch(
            @Valid @RequestBody ChurchRequest request) {

        ChurchResponse response = churchService.createChurch(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
