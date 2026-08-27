package com.obysoft.faithOS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChurchRequest {

    @NotBlank(message = "Church name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "CNPJ is required")
    private String cnpj;

    @NotBlank(message = "Principal Pastor is required")
    private String principalPastor;

    @Size(max = 200, message = "PIX key must contain at most 200 characters")
    private String pixKey;

    @Size(max = 25, message = "PIX recipient must contain at most 25 characters")
    private String pixRecipient;

    @Size(max = 15, message = "PIX city must contain at most 15 characters")
    private String pixCity;

    public ChurchRequest() {
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

    public String getPixKey() { return pixKey; }
    public void setPixKey(String pixKey) { this.pixKey = pixKey; }
    public String getPixRecipient() { return pixRecipient; }
    public void setPixRecipient(String pixRecipient) { this.pixRecipient = pixRecipient; }
    public String getPixCity() { return pixCity; }
    public void setPixCity(String pixCity) { this.pixCity = pixCity; }
}
