package com.obysoft.faithOS.repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndChurchId(Long id, Long churchId);
    List<User> findAllByIdInAndChurchId(Collection<Long> ids, Long churchId);

    boolean existsByEmail(String email);
    Page<User> findAllByChurchId(Long churchId, Pageable pageable);
    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);
    Page<User> findByChurchIdAndFirstNameContainingIgnoreCaseOrChurchIdAndLastNameContainingIgnoreCaseOrChurchIdAndEmailContainingIgnoreCase(
            Long firstNameChurchId, String firstName,
            Long lastNameChurchId, String lastName,
            Long emailChurchId, String email,
            Pageable pageable);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);

}
