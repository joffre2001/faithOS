package com.obysoft.faithOS.service;

import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.repository.ChurchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChurchService {

    private final ChurchRepository churchRepository;

    public ChurchService(ChurchRepository churchRepository) {
        this.churchRepository = churchRepository;
    }

    public Church save(Church church) {
        return churchRepository.save(church);
    }

    public List<Church> findAll() {
        return churchRepository.findAll();
    }
}