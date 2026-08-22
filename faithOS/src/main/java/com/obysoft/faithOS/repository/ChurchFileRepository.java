package com.obysoft.faithOS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.ChurchFile;

public interface ChurchFileRepository extends JpaRepository<ChurchFile, Long> {
    List<ChurchFile> findAllByChurchIdOrderByCreatedAtDesc(Long churchId);
    Optional<ChurchFile> findByIdAndChurchId(Long id, Long churchId);
}
