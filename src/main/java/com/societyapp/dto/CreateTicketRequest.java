package com.societyapp.dto;

import com.societyapp.entity.TicketCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTicketRequest {
    @NotBlank
    public String title;
    public String description;
    public String priority = "MEDIUM";
    public TicketCategory category;
    public String complaintPhotoUrl;
}
