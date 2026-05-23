package com.societyapp.dto;

import com.societyapp.entity.TicketCategory;
import com.societyapp.entity.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponse {
    public Long id;
    public String title;
    public String description;
    public TicketStatus status;
    public String priority;
    public TicketCategory category;
    public String flatNumber;
    public String creatorName;
    public String assignedStaffName;
    public String secretaryName;
    public String resolutionNote;
    public String complaintPhotoUrl;
    public String resolutionPhotoUrl;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public LocalDateTime resolvedAt;
}
