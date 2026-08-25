package com.obysoft.faithOS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.AbsenceMotivation;

public interface AbsenceMotivationRepository extends JpaRepository<AbsenceMotivation, Long> {

    List<AbsenceMotivation> findAllByChurchIdOrderByCreatedAtDesc(Long churchId);

    List<AbsenceMotivation> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<AbsenceMotivation> findByIdAndChurchId(Long id, Long churchId);
    long deleteByExpiresAtBefore(java.time.LocalDateTime cutoff);
}
