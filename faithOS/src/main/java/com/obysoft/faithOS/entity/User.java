package com.obysoft.faithOS.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;
    

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;
    @Column(length = 512) private String cpf;
    @Column(name = "cpf_hash", length = 64) private String cpfHash;
    @Column(name = "emergency_contact_name", length = 1024) private String emergencyContactName;
    @Column(name = "emergency_contact_phone", length = 512) private String emergencyContactPhone;
    @Column(name="member_code",length=50) private String memberCode;
    @Column(name="profile_picture_data", columnDefinition="bytea") private byte[] profilePictureData;
    @Column(name="profile_picture_content_type",length=50) private String profilePictureContentType;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCpfHash() { return cpfHash; }
    public void setCpfHash(String value) { this.cpfHash = value; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String value) { this.emergencyContactName = value; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String value) { this.emergencyContactPhone = value; }
    public String getMemberCode(){return memberCode;} public void setMemberCode(String value){memberCode=value;}
    public byte[] getProfilePictureData(){return profilePictureData;}
    public void setProfilePictureData(byte[] value){profilePictureData=value;}
    public String getProfilePictureContentType(){return profilePictureContentType;}
    public void setProfilePictureContentType(String value){profilePictureContentType=value;}

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean mustChangePassword = false;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getMustChangePassword() { return mustChangePassword; }

    public void setMustChangePassword(Boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }

    @ManyToOne
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    public Church getChurch() {
        return church;
    }

    public void setChurch(Church church) {
        this.church = church;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    

}
