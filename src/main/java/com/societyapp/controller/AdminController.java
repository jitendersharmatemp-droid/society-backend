package com.societyapp.controller;

import com.societyapp.dto.MessageResponse;
import com.societyapp.dto.UserResponse;
import com.societyapp.entity.AccountStatus;
import com.societyapp.entity.User;
import com.societyapp.repository.TicketRepository;
import com.societyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private TicketRepository ticketRepository;

    // GET all users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // GET users by role (e.g. ?role=ROLE_RESOLVER)
    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SECRETARY')")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            com.societyapp.entity.Role r = com.societyapp.entity.Role.valueOf(role);
            return ResponseEntity.ok(userRepository.findByRole(r)
                    .stream().map(this::toResponse).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid role: " + role));
        }
    }

    // SUSPEND a user
    @PostMapping("/suspend/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> suspendUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(AccountStatus.SUSPENDED);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse(user.getFullName() + " has been suspended"));
    }

    // ACTIVATE a user
    @PostMapping("/activate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse(user.getFullName() + " has been activated"));
    }

    // DELETE a user
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    private UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setFullName(u.getFullName());
        r.setPhoneNumber(u.getPhoneNumber());
        r.setFlatNumber(u.getFlatNumber());
        r.setWing(u.getWing());
        r.setRole(u.getRole().name());
        r.setAccountStatus(u.getAccountStatus().name());
        r.setActiveTickets(ticketRepository.countActiveByStaff(u));
        return r;
    }
}
