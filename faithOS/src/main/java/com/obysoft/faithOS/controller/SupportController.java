package com.obysoft.faithOS.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.SupportRequest;
import com.obysoft.faithOS.service.SupportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/support")
public class SupportController {
    private final SupportService service;
    public SupportController(SupportService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submit(@Valid @RequestBody SupportRequest request) { service.submit(request); }
}
