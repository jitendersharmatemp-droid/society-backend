package com.societyapp.dto;

import com.societyapp.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    public String username;
    @NotBlank
    @Email
    public String email;
    @NotBlank
    public String password;
    public String fullName;
    @NotBlank
    public String phoneNumber;
    public String flatNumber;   // required for FLAT_MEMBER
    public String wing;
    @NotNull
    public Role role;
}
