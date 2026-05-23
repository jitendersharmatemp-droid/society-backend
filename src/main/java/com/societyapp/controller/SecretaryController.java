package com.societyapp.controller;

import com.societyapp.dto.*;
import com.societyapp.entity.*;
import com.societyapp.repository.*;
import com.societyapp.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/secretary")
@CrossOrigin(origins = "*")
public class SecretaryController {

    @Autowired private UserRepository userRepository;
    @Autowired private TicketRepository ticketRepository;

    // GET all pending approval requests
    @GetMapping("/pending-approvals")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> getPendingApprovals() {
        List<User> pending = userRepository.findByAccountStatus(AccountStatus.PENDING);
        return ResponseEntity.ok(pending.stream().map(this::toUserResponse).collect(Collectors.toList()));
    }

    // APPROVE a flat member
    @PostMapping("/approve/{userId}")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDetailsImpl currentUser = (UserDetailsImpl)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setApprovedBy(currentUser.getId());
        user.setApprovedAt(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User " + user.getFullName() + " approved successfully"));
    }

    // REJECT a flat member
    @PostMapping("/reject/{userId}")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> rejectUser(@PathVariable Long userId, @RequestBody ApprovalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(AccountStatus.REJECTED);
        user.setRejectionReason(request.getRejectionReason());
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User rejected"));
    }

    // GET all maintenance staff with workload count
    @GetMapping("/staff-workload")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> getStaffWorkload() {
        List<User> staffList = userRepository.findByRoleAndAccountStatus(
                Role.ROLE_MAINTENANCE_STAFF, AccountStatus.ACTIVE);
        List<UserResponse> result = staffList.stream().map(staff -> {
            UserResponse r = toUserResponse(staff);
            r.setActiveTickets(ticketRepository.countActiveByStaff(staff));
            return r;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // GET all active flat members
    @GetMapping("/flat-members")
    @PreAuthorize("hasRole('SECRETARY') or hasRole('ADMIN')")
    public ResponseEntity<?> getFlatMembers() {
        List<User> members = userRepository.findByRoleAndAccountStatus(
                Role.ROLE_FLAT_MEMBER, AccountStatus.ACTIVE);
        return ResponseEntity.ok(members.stream().map(this::toUserResponse).collect(Collectors.toList()));
    }

    private UserResponse toUserResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId()); r.setUsername(u.getUsername());
        r.setEmail(u.getEmail()); r.setFullName(u.getFullName());
        r.setPhoneNumber(u.getPhoneNumber());
        r.setFlatNumber(u.getFlatNumber()); r.setWing(u.getWing());
        r.setRole(u.getRole().name());
        r.setAccountStatus(u.getAccountStatus().name());
        return r;
    }
}
