package com.societyapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

// ── AUTH ─────────────────────────────────────────────────────

// ── TICKET ───────────────────────────────────────────────────

@Data
public class ResolveTicketRequest {
    @NotBlank public String resolutionNote;
    public String resolutionPhotoUrl;
}

// ── USER ─────────────────────────────────────────────────────

// ── GENERIC ──────────────────────────────────────────────────

