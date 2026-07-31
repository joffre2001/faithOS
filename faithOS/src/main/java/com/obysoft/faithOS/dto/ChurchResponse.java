package com.obysoft.faithOS.dto;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.Church;

public class ChurchResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String cnpj;
    private String principalPastor;

    public ChurchResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getPrincipalPastor() {
        return principalPastor;
    }

    public void setPrincipalPastor(String principalPastor) {
        this.principalPastor = principalPastor;
    }



    
    public interface ChurchRepository extends JpaRepository<Church, Long> {
    
        Optional<Church> findByEmail(String email);
    
        Optional<Church> findByCnpj(String cnpj);
    
    }


}