package com.societyapp.service;

import com.societyapp.dto.*;
import com.societyapp.entity.*;
import com.societyapp.repository.UserRepository;
import com.societyapp.security.JwtUtils;
import com.societyapp.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Block PENDING or REJECTED users from getting a token
        if (!userDetails.getAccountStatus().equals("ACTIVE")) {
            if (userDetails.getAccountStatus().equals("PENDING")) {
                throw new RuntimeException("Your account is pending approval by the Secretary. Please wait.");
            }
            throw new RuntimeException("Your account has been rejected or suspended. Contact the Secretary.");
        }

        String jwt = jwtUtils.generateJwtToken(authentication);
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), userDetails.getRole(),
                userDetails.getAccountStatus(), userDetails.getFlatNumber());
    }

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already taken!");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered!");
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber()))
            throw new RuntimeException("Phone number already registered!");

        // One account per flat - only for flat members
        if (request.getRole() == Role.ROLE_FLAT_MEMBER && request.getFlatNumber() != null) {
            if (userRepository.existsByFlatNumber(request.getFlatNumber()))
                throw new RuntimeException("An account already exists for flat " + request.getFlatNumber());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .flatNumber(request.getFlatNumber())
                .wing(request.getWing())
                .role(request.getRole())
                // Staff and secretaries are auto-approved (added by admin manually)
                // Flat members must be approved by secretary
                .accountStatus(request.getRole() == Role.ROLE_FLAT_MEMBER
                        ? AccountStatus.PENDING : AccountStatus.ACTIVE)
                .build();

        userRepository.save(user);

        String message = request.getRole() == Role.ROLE_FLAT_MEMBER
                ? "Registration successful! Your account is pending Secretary approval. You will be notified once approved."
                : "Account created successfully!";

        return new MessageResponse(message);
    }
}
