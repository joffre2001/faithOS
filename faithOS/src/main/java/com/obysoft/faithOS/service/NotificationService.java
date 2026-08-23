package com.obysoft.faithOS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.dto.NotificationRequest;
import com.obysoft.faithOS.dto.NotificationResponse;
import com.obysoft.faithOS.entity.Notification;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final CurrentChurchService current;

    public NotificationService(NotificationRepository repository, CurrentChurchService current) {
        this.repository = repository;
        this.current = current;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> all() {
        User user = current.user();
        return repository.findAllByChurchIdOrderByCreatedAtDesc(user.getChurch().getId())
                .stream().map(notification -> response(notification, user)).toList();
    }

    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.title().trim());
        notification.setMessage(request.message().trim());
        notification.setType(request.type().trim().toUpperCase());
        notification.setChurch(current.church());
        return response(repository.save(notification), current.user());
    }

    @Transactional
    public NotificationResponse update(Long id, NotificationRequest request) {
        User user = current.user();
        Notification notification = find(id, user.getChurch().getId());
        notification.setTitle(request.title().trim());
        notification.setMessage(request.message().trim());
        notification.setType(request.type().trim().toUpperCase());
        return response(repository.save(notification), user);
    }

    @Transactional
    public NotificationResponse markRead(Long id) {
        User user = current.user();
        Notification notification = find(id, user.getChurch().getId());
        notification.getReadBy().add(user);
        return response(repository.save(notification), user);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id, current.church().getId()));
    }

    private Notification find(Long id, Long churchId) {
        return repository.findByIdAndChurchId(id, churchId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
    }

    private NotificationResponse response(Notification notification, User user) {
        return new NotificationResponse(notification.getId(), notification.getTitle(), notification.getMessage(),
                notification.getType(), notification.getCreatedAt(), notification.getReadBy().contains(user));
    }
}
