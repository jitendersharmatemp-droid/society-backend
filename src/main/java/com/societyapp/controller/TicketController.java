package com.societyapp.controller;

import com.societyapp.dto.*;
import com.societyapp.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired private TicketService ticketService;

    // FLAT MEMBER
    @PostMapping("/create")
    @PreAuthorize("hasRole('FLAT_MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<?> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        try { return ResponseEntity.ok(ticketService.createTicket(request)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())); }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('FLAT_MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyTickets() {
        return ResponseEntity.ok(ticketService.getMyTickets());
    }

    // SECRETARY / ADMIN
    @GetMapping("/all")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PostMapping("/pool/{id}")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> moveToPool(@PathVariable Long id) {
        try { return ResponseEntity.ok(ticketService.moveToPool(id)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())); }
    }

    @PostMapping("/assign/{id}")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> assignTicket(@PathVariable Long id, @Valid @RequestBody AssignTicketRequest request) {
        try { return ResponseEntity.ok(ticketService.assignTicket(id, request)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())); }
    }

    @PostMapping("/close/{id}")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> closeTicket(@PathVariable Long id) {
        try { return ResponseEntity.ok(ticketService.closeTicket(id)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())); }
    }

    // MAINTENANCE STAFF
    @GetMapping("/pool")
    @PreAuthorize("hasRole('MAINTENANCE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getPoolTickets() {
        return ResponseEntity.ok(ticketService.getPoolTickets());
    }

    @PostMapping("/pick/{id}")
    @PreAuthorize("hasRole('MAINTENANCE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> pickTicket(@PathVariable Long id) {
        try { return ResponseEntity.ok(ticketService.pickTicket(id)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())); }
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('MAINTENANCE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyAssignedTickets() {
        return ResponseEntity.ok(ticketService.getMyAssignedTickets());
    }

    @PostMapping("/resolve/{id}")
    @PreAuthorize("hasRole('MAINTENANCE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> resolveTicket(@PathVariable Long id, @Valid @RequestBody ResolveTicketRequest request) {
        try { return ResponseEntity.ok(ticketService.resolveTicket(id, request)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())); }
    }

    // ANY AUTHENTICATED USER
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicket(@PathVariable Long id) {
        try { return ResponseEntity.ok(ticketService.getTicket(id)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())); }
    }
}
