package com.obysoft.faithOS.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.obysoft.faithOS.dto.UserRequest;
import com.obysoft.faithOS.dto.UserResponse;
import com.obysoft.faithOS.dto.UserUpdateRequest;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.DuplicateResourceException;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChurchRepository churchRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final SensitiveDataService sensitiveData;

    @Autowired
    public UserService(
            UserRepository userRepository,
            ChurchRepository churchRepository,
            PasswordEncoder passwordEncoder,
            PasswordResetService passwordResetService,
            SensitiveDataService sensitiveData) {

        this.userRepository = userRepository;
        this.churchRepository = churchRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
        this.sensitiveData = sensitiveData;
    }

    UserService(UserRepository userRepository, ChurchRepository churchRepository,
            PasswordEncoder passwordEncoder, PasswordResetService passwordResetService) {
        this(userRepository, churchRepository, passwordEncoder, passwordResetService,
                new SensitiveDataService("test-only-sensitive-data-key-32chars"));
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists."
            );
        }

        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() == Role.CHURCH_ADMIN
                && !authenticatedUser.getChurch().getId()
                        .equals(request.getChurchId())) {

            throw new AccessDeniedException(
                    "You cannot create users for another church."
            );
        }

        if (authenticatedUser.getRole() != Role.SUPER_ADMIN
                && request.getRole() == Role.SUPER_ADMIN) {

            throw new AccessDeniedException(
                    "Only a super administrator can create a super administrator."
            );
        }

        Church church = churchRepository.findById(request.getChurchId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Church not found with id: "
                        + request.getChurchId()
                )
                );

        User user = new User();

        String cpf = normalizeCpf(request.getCpf());
        String cpfHash = sensitiveData.hash(cpf);
        if (userRepository.existsByCpfHash(cpfHash)) {
            throw new DuplicateResourceException("CPF already exists.");
        }
        String memberCode=normalizeMemberCode(request.getMemberCode());
        if(userRepository.existsByChurchIdAndMemberCode(request.getChurchId(),memberCode))throw new DuplicateResourceException("Member ID already exists in this church.");

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setPhone(request.getPhone());
        user.setCpf(sensitiveData.encrypt(cpf));
        user.setCpfHash(cpfHash);
        user.setEmergencyContactName(sensitiveData.encrypt(request.getEmergencyContactName().trim()));
        user.setEmergencyContactPhone(sensitiveData.encrypt(request.getEmergencyContactPhone().trim()));
        user.setMemberCode(memberCode);
        user.setRole(request.getRole());
        user.setChurch(church);
        user.setMustChangePassword(true);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public Page<UserResponse> findAll(Pageable pageable, String search) {
        User authenticatedUser = getAuthenticatedUser();

        String query = search == null ? "" : search.trim();
        Page<User> users;

        if (authenticatedUser.getRole() == Role.SUPER_ADMIN) {
            users = query.isEmpty()
                    ? userRepository.findAll(pageable)
                    : userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            query, query, query, pageable);
        } else {
            Long churchId = authenticatedUser.getChurch().getId();
            users = query.isEmpty()
                    ? userRepository.findAllByChurchId(churchId, pageable)
                    : userRepository.findByChurchIdAndFirstNameContainingIgnoreCaseOrChurchIdAndLastNameContainingIgnoreCaseOrChurchIdAndEmailContainingIgnoreCase(
                            churchId, query, churchId, query, churchId, query, pageable);
        }

        return users.map(this::toResponse);
    }

    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request) {

        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "User not found with id: " + id
                )
                );

        if (authenticatedUser.getRole() == Role.CHURCH_ADMIN
                && !authenticatedUser.getChurch().getId()
                        .equals(user.getChurch().getId())) {

            throw new AccessDeniedException(
                    "You cannot update a user from another church."
            );
        }

        if (authenticatedUser.getRole() != Role.SUPER_ADMIN
                && request.getRole() == Role.SUPER_ADMIN) {

            throw new AccessDeniedException(
                    "Only a super administrator can assign this role."
            );
        }

        if (userRepository.existsByEmailAndIdNot(
                request.getEmail(), id)) {

            throw new DuplicateResourceException(
                    "Email already exists."
            );
        }

        String cpf = normalizeCpf(request.getCpf());
        String cpfHash = sensitiveData.hash(cpf);
        if (userRepository.existsByCpfHashAndIdNot(cpfHash, id)) {
            throw new DuplicateResourceException("CPF already exists.");
        }
        String memberCode=normalizeMemberCode(request.getMemberCode());
        if(userRepository.existsByChurchIdAndMemberCodeAndIdNot(user.getChurch().getId(),memberCode,id))throw new DuplicateResourceException("Member ID already exists in this church.");

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setCpf(sensitiveData.encrypt(cpf));
        user.setCpfHash(cpfHash);
        user.setEmergencyContactName(sensitiveData.encrypt(request.getEmergencyContactName().trim()));
        user.setEmergencyContactPhone(sensitiveData.encrypt(request.getEmergencyContactPhone().trim()));
        user.setMemberCode(memberCode);
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public UserResponse updateUserStatus(
            Long id,
            Boolean active) {

        User authenticatedUser = getAuthenticatedUser();

        User user = userRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "User not found with id: " + id
                )
                );

        if (authenticatedUser.getId().equals(user.getId())
                && Boolean.FALSE.equals(active)) {

            throw new AccessDeniedException(
                    "You cannot deactivate your own account."
            );
        }

        if (authenticatedUser.getRole() == Role.CHURCH_ADMIN
                && !authenticatedUser.getChurch().getId()
                        .equals(user.getChurch().getId())) {

            throw new AccessDeniedException(
                    "You cannot manage a user from another church."
            );
        }

        if (authenticatedUser.getRole() != Role.SUPER_ADMIN
                && user.getRole() == Role.SUPER_ADMIN) {

            throw new AccessDeniedException(
                    "Only a super administrator can manage this account."
            );
        }

        user.setActive(active);

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public void sendInvitation(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (authenticatedUser.getRole() != Role.SUPER_ADMIN
                && !authenticatedUser.getChurch().getId().equals(user.getChurch().getId())) {
            throw new AccessDeniedException("You cannot invite a user from another church.");
        }

        if (authenticatedUser.getRole() != Role.SUPER_ADMIN && user.getRole() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Only a super administrator can invite this account.");
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalArgumentException("Activate this account before sending an invitation.");
        }
        passwordResetService.invite(user.getEmail());
    }

    public UserResponse getCurrentUser() {
        return toResponse(getAuthenticatedUser());
    }

    private User getAuthenticatedUser() {

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Authenticated user not found."
                )
                );
    }

    private UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCpf(sensitiveData.decrypt(user.getCpf()));
        response.setEmergencyContactName(sensitiveData.decrypt(user.getEmergencyContactName()));
        response.setEmergencyContactPhone(sensitiveData.decrypt(user.getEmergencyContactPhone()));
        response.setMemberCode(user.getMemberCode());
        response.setRole(user.getRole());
        response.setActive(user.getActive());
        response.setMustChangePassword(user.getMustChangePassword());

        if (user.getChurch() != null) {
            response.setChurchName(
                    user.getChurch().getName()
            );
        }

        return response;
    }

    private String normalizeCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }
    private String normalizeMemberCode(String value){return value.trim().toUpperCase();}
}
