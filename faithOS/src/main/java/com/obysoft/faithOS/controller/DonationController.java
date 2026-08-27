package com.obysoft.faithOS.controller;

import java.util.Map;

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
import com.obysoft.faithOS.service.CurrentChurchService;
import com.obysoft.faithOS.service.SensitiveDataService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/donations/pix")
public class DonationController {
    private final ContributionService contributions;
    private final CurrentChurchService currentChurch;
    private final SensitiveDataService sensitiveData;

    public DonationController(ContributionService contributions, CurrentChurchService currentChurch,
            SensitiveDataService sensitiveData) {
        this.contributions = contributions;
        this.currentChurch = currentChurch;
        this.sensitiveData = sensitiveData;
    }

    @GetMapping
    public Map<String, String> configuration() {
        var church = currentChurch.church();
        String key = sensitiveData.decrypt(church.getPixKey());
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Your church administrator has not configured a PIX key yet.");
        }
        String recipient = church.getPixRecipient() == null || church.getPixRecipient().isBlank()
                ? church.getName() : church.getPixRecipient();
        String city = church.getPixCity() == null || church.getPixCity().isBlank()
                ? "BRASIL" : church.getPixCity();
        return Map.of("key", key, "recipient", recipient, "city", city);
    }

    @PostMapping
    public ResponseEntity<ContributionResponse> record(@Valid @RequestBody PixDonationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contributions.createMemberPix(request));
    }
}
