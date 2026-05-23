package com.societyapp.entity;

public enum AccountStatus {
    PENDING,   // Waiting for secretary/admin approval
    ACTIVE,    // Approved - can raise tickets
    REJECTED,  // Rejected - permanently blocked
    SUSPENDED  // Temporarily suspended by admin
}
