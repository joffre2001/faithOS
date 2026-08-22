package com.obysoft.faithOS.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.dto.LoginResponse;
import com.obysoft.faithOS.dto.SetupRequest;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.DuplicateResourceException;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.UserRepository;
import com.obysoft.faithOS.security.JwtService;

@Service
public class SetupService {
    private final UserRepository userRepository;
    private final ChurchRepository churchRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SetupService(UserRepository userRepository, ChurchRepository churchRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.churchRepository = churchRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public boolean isAvailable() { return userRepository.count() == 0; }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LoginResponse initialize(SetupRequest request) {
        if (userRepository.count() != 0) {
            throw new DuplicateResourceException("FaithOS has already been configured.");
        }

        Church church = new Church();
        church.setName(request.getChurchName().trim());
        church.setEmail(request.getEmail().trim().toLowerCase());
        church.setPhone(request.getPhone());
        church.setPrincipalPastor(request.getFirstName().trim() + " " + request.getLastName().trim());
        church = churchRepository.save(church);

        User admin = new User();
        admin.setFirstName(request.getFirstName().trim());
        admin.setLastName(request.getLastName().trim());
        admin.setEmail(request.getEmail().trim().toLowerCase());
        admin.setPhone(request.getPhone());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.CHURCH_ADMIN);
        admin.setActive(true);
        admin.setChurch(church);
        admin = userRepository.save(admin);

        LoginResponse response = new LoginResponse();
        response.setId(admin.getId());
        response.setFullName(admin.getFirstName() + " " + admin.getLastName());
        response.setEmail(admin.getEmail());
        response.setRole(admin.getRole().name());
        response.setChurchId(church.getId());
        response.setChurchName(church.getName());
        response.setMessage("FaithOS setup completed.");
        response.setToken(jwtService.generateToken(admin));
        return response;
    }
}
