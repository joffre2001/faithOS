package com.obysoft.faithOS.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.obysoft.faithOS.dto.UserRequest;
import com.obysoft.faithOS.dto.UserResponse;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChurchRepository churchRepository;

    public UserService(UserRepository userRepository,
                       ChurchRepository churchRepository) {
        this.userRepository = userRepository;
        this.churchRepository = churchRepository;
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        Church church = churchRepository.findById(request.getChurchId())
                .orElseThrow(() -> new RuntimeException("Church not found."));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // We'll encrypt this later
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setChurch(church);

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setRole(savedUser.getRole());
        response.setActive(savedUser.getActive());
        response.setChurchName(savedUser.getChurch().getName());

        return response;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}