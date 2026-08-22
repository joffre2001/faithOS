package com.obysoft.faithOS.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
public class ChurchRegistrationService {
    private final ChurchRepository churchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ChurchRegistrationService(ChurchRepository churchRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.churchRepository = churchRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse register(SetupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account already exists for this email.");
        }
        if (churchRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("A church already exists for this email.");
        }

        Church church = new Church();
        church.setName(request.getChurchName().trim());
        church.setEmail(email);
        church.setPhone(request.getPhone());
        church.setPrincipalPastor(request.getFirstName().trim() + " " + request.getLastName().trim());
        church = churchRepository.save(church);

        User admin = new User();
        admin.setFirstName(request.getFirstName().trim());
        admin.setLastName(request.getLastName().trim());
        admin.setEmail(email);
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
        response.setMessage("Church account created.");
        response.setToken(jwtService.generateToken(admin));
        return response;
    }
}
