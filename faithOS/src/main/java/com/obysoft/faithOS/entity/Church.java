package com.obysoft.faithOS.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "churches")
public class Church {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @jakarta.persistence.Column(name = "logo_data", columnDefinition = "bytea")
    private byte[] logoData;

    @jakarta.persistence.Column(name = "logo_content_type", length = 50)
    private String logoContentType;

    @jakarta.persistence.Column(nullable = false)
    private Boolean active = true;
    

    @OneToMany(mappedBy = "church")
    private List<User> users;

    public Church() {
    }

    public Long getId() {
        return id;
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

    public byte[] getLogoData() { return logoData; }
    public void setLogoData(byte[] value) { this.logoData = value; }
    public String getLogoContentType() { return logoContentType; }
    public void setLogoContentType(String value) { this.logoContentType = value; }

    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getMembers() {
        return users;
    }

    public void setMembers(List<User> members) {
        this.users = members;
    }
}
