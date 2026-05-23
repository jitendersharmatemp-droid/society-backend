package com.societyapp.service;

import com.societyapp.dto.*;
import com.societyapp.entity.*;
import com.societyapp.repository.*;
import com.societyapp.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired private TicketRepository ticketRepository;
    @Autowired private UserRepository userRepository;

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // FLAT MEMBER: Create ticket
    public TicketResponse createTicket(CreateTicketRequest request) {
        User creator = getCurrentUser();
        if (creator.getAccountStatus() != AccountStatus.ACTIVE)
            throw new RuntimeException("Your account is not approved yet.");

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(request.getCategory())
                .flatNumber(creator.getFlatNumber()) // auto-fill from profile
                .complaintPhotoUrl(request.getComplaintPhotoUrl())
                .status(TicketStatus.OPEN)
                .creator(creator)
                .build();
        return toResponse(ticketRepository.save(ticket));
    }

    // FLAT MEMBER: My tickets
    public List<TicketResponse> getMyTickets() {
        return ticketRepository.findByCreator(getCurrentUser())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // SECRETARY: All tickets
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // SECRETARY: Move to pool
    public TicketResponse moveToPool(Long ticketId) {
        User secretary = getCurrentUser();
        Ticket ticket = getById(ticketId);
        if (ticket.getStatus() != TicketStatus.OPEN)
            throw new RuntimeException("Only OPEN tickets can be moved to pool");
        ticket.setStatus(TicketStatus.IN_POOL);
        ticket.setSecretary(secretary);
        return toResponse(ticketRepository.save(ticket));
    }

    // SECRETARY: Assign to specific staff
    public TicketResponse assignTicket(Long ticketId, AssignTicketRequest request) {
        User secretary = getCurrentUser();
        Ticket ticket = getById(ticketId);
        if (ticket.getStatus() != TicketStatus.OPEN && ticket.getStatus() != TicketStatus.IN_POOL)
            throw new RuntimeException("Cannot assign ticket in status: " + ticket.getStatus());

        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        if (staff.getRole() != Role.ROLE_MAINTENANCE_STAFF)
            throw new RuntimeException("Target user is not maintenance staff");

        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setAssignedStaff(staff);
        ticket.setSecretary(secretary);
        return toResponse(ticketRepository.save(ticket));
    }

    // MAINTENANCE STAFF: View pool
    public List<TicketResponse> getPoolTickets() {
        return ticketRepository.findPoolTickets()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // MAINTENANCE STAFF: Self-pick from pool
    public TicketResponse pickTicket(Long ticketId) {
        User staff = getCurrentUser();
        Ticket ticket = getById(ticketId);
        if (ticket.getStatus() != TicketStatus.IN_POOL)
            throw new RuntimeException("Ticket is not in pool");
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setAssignedStaff(staff);
        return toResponse(ticketRepository.save(ticket));
    }

    // MAINTENANCE STAFF: My assigned tickets
    public List<TicketResponse> getMyAssignedTickets() {
        return ticketRepository.findByAssignedStaff(getCurrentUser())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // MAINTENANCE STAFF: Resolve ticket
    public TicketResponse resolveTicket(Long ticketId, ResolveTicketRequest request) {
        User staff = getCurrentUser();
        Ticket ticket = getById(ticketId);
        if (!ticket.getAssignedStaff().getId().equals(staff.getId()))
            throw new RuntimeException("You are not assigned to this ticket");
        if (ticket.getStatus() != TicketStatus.IN_PROGRESS && ticket.getStatus() != TicketStatus.ASSIGNED)
            throw new RuntimeException("Ticket is not in a resolvable state");
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolutionNote(request.getResolutionNote());
        ticket.setResolutionPhotoUrl(request.getResolutionPhotoUrl());
        ticket.setResolvedAt(LocalDateTime.now());
        return toResponse(ticketRepository.save(ticket));
    }

    // SECRETARY/ADMIN: Close ticket
    public TicketResponse closeTicket(Long ticketId) {
        Ticket ticket = getById(ticketId);
        ticket.setStatus(TicketStatus.CLOSED);
        return toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse getTicket(Long ticketId) {
        return toResponse(getById(ticketId));
    }

    private Ticket getById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));
    }

    private TicketResponse toResponse(Ticket t) {
        TicketResponse r = new TicketResponse();
        r.setId(t.getId()); r.setTitle(t.getTitle());
        r.setDescription(t.getDescription()); r.setStatus(t.getStatus());
        r.setPriority(t.getPriority()); r.setCategory(t.getCategory());
        r.setFlatNumber(t.getFlatNumber());
        r.setComplaintPhotoUrl(t.getComplaintPhotoUrl());
        r.setResolutionPhotoUrl(t.getResolutionPhotoUrl());
        r.setResolutionNote(t.getResolutionNote());
        r.setCreatedAt(t.getCreatedAt()); r.setUpdatedAt(t.getUpdatedAt());
        r.setResolvedAt(t.getResolvedAt());
        if (t.getCreator() != null) r.setCreatorName(t.getCreator().getFullName());
        if (t.getAssignedStaff() != null) r.setAssignedStaffName(t.getAssignedStaff().getFullName());
        if (t.getSecretary() != null) r.setSecretaryName(t.getSecretary().getFullName());
        return r;
    }
}
