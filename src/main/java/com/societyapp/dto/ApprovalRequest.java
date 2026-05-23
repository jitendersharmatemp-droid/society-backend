package com.societyapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalRequest {
    @NotNull
    public Long userId;
    public String rejectionReason;
}
