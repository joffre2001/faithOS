package com.obysoft.faithOS.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.AttendanceSession;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    List<AttendanceSession> findAllByChurchIdOrderBySessionDateDesc(Long churchId);
    List<AttendanceSession> findAllByChurchIdAndSessionDateBetweenOrderBySessionDateDesc(
            Long churchId, LocalDate from, LocalDate to);
    Optional<AttendanceSession> findByIdAndChurchId(Long id, Long churchId);
    List<AttendanceSession> findAllByChurchIdAndSessionDateOrderByOpensAtDesc(Long churchId, LocalDate sessionDate);
}
