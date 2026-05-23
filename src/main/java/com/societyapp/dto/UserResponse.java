package com.societyapp.dto;

import lombok.Data;

@Data
public class UserResponse {
    public Long id;
    public String username;
    public String email;
    public String fullName;
    public String phoneNumber;
    public String flatNumber;
    public String wing;
    public String role;
    public String accountStatus;
    public long activeTickets;
}
