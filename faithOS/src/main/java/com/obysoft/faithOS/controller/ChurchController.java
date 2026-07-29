package com.obysoft.faithOS.controller;

import com.obysoft.faithOS.dto.ChurchRequest;
import com.obysoft.faithOS.dto.ChurchResponse;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.service.ChurchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/churches")
public class ChurchController {

    private final ChurchService churchService;

    public ChurchController(ChurchService churchService) {
        this.churchService = churchService;
    }

    @PostMapping
    public ChurchResponse create(@Valid @RequestBody ChurchRequest request) {

        Church church = new Church();
        church.setName(request.getName());
        church.setEmail(request.getEmail());
        church.setPhone(request.getPhone());
        church.setAddress(request.getAddress());

        Church saved = churchService.save(church);

        return new ChurchResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getAddress()
        );
    }

    @GetMapping
    public List<Church> findAll() {
        return churchService.findAll();
    }
}