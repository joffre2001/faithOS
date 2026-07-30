package com.obysoft.faithOS.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
public class ChurchController {

    private final ChurchService churchService;

    public ChurchController(ChurchService churchService) {
        this.churchService = churchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChurchResponse createChurch(@Valid @RequestBody ChurchRequest request) {
        return churchService.createChurch(request);
    }

    @GetMapping("/{id}")
    public ChurchResponse getChurchById(@PathVariable Long id) {
        return churchService.getChurchById(id);
    }
   
}