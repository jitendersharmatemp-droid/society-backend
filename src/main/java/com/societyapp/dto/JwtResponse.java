package com.societyapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    public String token;
    public String type = "Bearer";
    public Long id;
    public String username;
    public String email;
    public String role;
    public String accountStatus;
    public String flatNumber;

    public JwtResponse(String token, Long id, String username, String email,
                       String role, String accountStatus, String flatNumber) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.accountStatus = accountStatus;
        this.flatNumber = flatNumber;
    }
}
