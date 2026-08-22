package com.obysoft.faithOS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByChurchIdOrderByCreatedAtDesc(Long churchId);
    Optional<Notification> findByIdAndChurchId(Long id, Long churchId);
}
