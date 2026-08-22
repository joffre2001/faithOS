package com.obysoft.faithOS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.Church;

public interface ChurchRepository extends JpaRepository<Church, Long> {

    Optional<Church> findByEmail(String email);

    Optional<Church> findByCnpj(String cnpj);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);

}
