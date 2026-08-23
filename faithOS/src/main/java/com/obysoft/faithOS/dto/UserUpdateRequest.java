package com.obysoft.faithOS.dto;

import com.obysoft.faithOS.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.obysoft.faithOS.validation.ValidCpf;

public class UserUpdateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    private String phone;
    @NotBlank(message = "CPF is required") @Pattern(regexp = "(?:\\d{3}\\.?){3}-?\\d{2}|\\d{11}", message = "CPF must contain 11 digits") @ValidCpf private String cpf;
    @NotBlank(message = "Emergency contact name is required") private String emergencyContactName;
    @NotBlank(message = "Emergency contact phone is required") private String emergencyContactPhone;

    @NotNull(message = "Role is required")
    private Role role;

    public UserUpdateRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
    public String getCpf() { return cpf; }
    public void setCpf(String value) { this.cpf = value; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String value) { this.emergencyContactName = value; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String value) { this.emergencyContactPhone = value; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
