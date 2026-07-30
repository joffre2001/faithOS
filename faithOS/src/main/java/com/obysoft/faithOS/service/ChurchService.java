package com.obysoft.faithOS.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.obysoft.faithOS.dto.ChurchRequest;
import com.obysoft.faithOS.dto.ChurchResponse;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.repository.ChurchRepository;

@Service
public class ChurchService {

    private final ChurchRepository churchRepository;

    public ChurchService(ChurchRepository churchRepository) {
        this.churchRepository = churchRepository;
    }

    public List<ChurchResponse> findAll() {
        return churchRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ChurchResponse createChurch(ChurchRequest request) {

        Church church = new Church();

        church.setName(request.getName());
        church.setEmail(request.getEmail());
        church.setPhone(request.getPhone());
        church.setAddress(request.getAddress());
        church.setCnpj(request.getCnpj());
        church.setPrincipalPastor(request.getPrincipalPastor());

        Church savedChurch = churchRepository.save(church);

        return toResponse(savedChurch);
    }

    public ChurchResponse getChurchById(Long id) {

        Church church = churchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Church not found with id: " + id));

        return toResponse(church);
    }

    private ChurchResponse toResponse(Church church) {

        ChurchResponse response = new ChurchResponse();

        response.setId(church.getId());
        response.setName(church.getName());
        response.setEmail(church.getEmail());
        response.setPhone(church.getPhone());
        response.setAddress(church.getAddress());
        response.setCnpj(church.getCnpj());
        response.setPrincipalPastor(church.getPrincipalPastor());

        return response;
    }
}