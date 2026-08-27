package com.obysoft.faithOS.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.obysoft.faithOS.dto.ChurchRequest;
import com.obysoft.faithOS.dto.ChurchResponse;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.exception.DuplicateResourceException;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.ChurchRepository;

@Service
public class ChurchService {

    private final ChurchRepository churchRepository;
    private final SensitiveDataService sensitiveData;

    public ChurchService(ChurchRepository churchRepository, SensitiveDataService sensitiveData) {
        this.churchRepository = churchRepository;
        this.sensitiveData = sensitiveData;
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
        applyPixSettings(church, request);

        if (churchRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered.");

        }

        if (churchRepository.findByCnpj(request.getCnpj()).isPresent()) {
            throw new DuplicateResourceException("CNPJ already registered.");
        }

        Church savedChurch = churchRepository.save(church);

        return toResponse(savedChurch);

    }

    public ChurchResponse getChurchById(Long id) {

        Church church = churchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Church not found with id: " + id));

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
        response.setPixKey(sensitiveData.decrypt(church.getPixKey()));
        response.setPixRecipient(church.getPixRecipient());
        response.setPixCity(church.getPixCity());

        return response;
    }

    public List<ChurchResponse> getAllChurches() {

        return churchRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ChurchResponse updateChurch(Long id, ChurchRequest request) {

        Church church = churchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Church not found with id: " + id));

        if (churchRepository.existsByEmailAndIdNot(
                request.getEmail(), id)) {

            throw new DuplicateResourceException(
                    "Email already registered."
            );
        }

        if (churchRepository.existsByCnpjAndIdNot(
                request.getCnpj(), id)) {

            throw new DuplicateResourceException(
                    "CNPJ already registered."
            );
        }

        church.setName(request.getName());
        church.setEmail(request.getEmail());
        church.setPhone(request.getPhone());
        church.setAddress(request.getAddress());
        church.setCnpj(request.getCnpj());
        church.setPrincipalPastor(request.getPrincipalPastor());
        applyPixSettings(church, request);

        Church updated = churchRepository.save(church);

        return toResponse(updated);
    }

    public void deleteChurch(Long id) {

        Church church = churchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Church not found with id: " + id));

        churchRepository.delete(church);
    }

    private void applyPixSettings(Church church, ChurchRequest request) {
        church.setPixKey(sensitiveData.encrypt(trimToNull(request.getPixKey())));
        church.setPixRecipient(trimToNull(request.getPixRecipient()));
        church.setPixCity(trimToNull(request.getPixCity()));
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

}
