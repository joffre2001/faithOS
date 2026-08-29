package com.obysoft.faithOS.dto;



public class ChurchResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String cnpj;
    private String principalPastor;
    private String pixKey;
    private String pixRecipient;
    private String pixCity;
    private String logoUrl;

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

    public String getPixKey() { return pixKey; }
    public void setPixKey(String pixKey) { this.pixKey = pixKey; }
    public String getPixRecipient() { return pixRecipient; }
    public void setPixRecipient(String pixRecipient) { this.pixRecipient = pixRecipient; }
    public String getPixCity() { return pixCity; }
    public void setPixCity(String pixCity) { this.pixCity = pixCity; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String value) { this.logoUrl = value; }

}
