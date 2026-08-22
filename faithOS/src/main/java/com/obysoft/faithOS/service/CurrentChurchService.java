package com.obysoft.faithOS.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class CurrentChurchService {
    private final UserRepository userRepository;
    public CurrentChurchService(UserRepository userRepository) { this.userRepository = userRepository; }
    public User user() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }
    public Church church() {
        Church church = user().getChurch();
        if (church == null) throw new ResourceNotFoundException("Authenticated user is not linked to a church.");
        return church;
    }
}
