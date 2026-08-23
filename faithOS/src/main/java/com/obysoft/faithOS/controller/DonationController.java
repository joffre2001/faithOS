package com.obysoft.faithOS.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.ContributionResponse;
import com.obysoft.faithOS.dto.PixDonationRequest;
import com.obysoft.faithOS.service.ContributionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/donations/pix")
public class DonationController {
    private final ContributionService contributions;
    private final String pixKey;

    public DonationController(ContributionService contributions,
            @Value("${app.donations.pix-key:eglisedechapeco2000@gmail.com}") String pixKey) {
        this.contributions = contributions;
        this.pixKey = pixKey;
    }

    @GetMapping
    public Map<String, String> configuration() {
        return Map.of("key", pixKey, "recipient", "Eglise de Chapeco", "city", "CHAPECO");
    }

    @PostMapping
    public ResponseEntity<ContributionResponse> record(@Valid @RequestBody PixDonationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contributions.createMemberPix(request));
    }
}
