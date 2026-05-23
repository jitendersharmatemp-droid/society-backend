package com.societyapp.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.societyapp.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String username;
    private final String email;
    private final String role;
    private final String flatNumber;
    private final String accountStatus;
    @JsonIgnore private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           String role, String flatNumber, String accountStatus,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id; this.username = username; this.email = email;
        this.password = password; this.role = role;
        this.flatNumber = flatNumber; this.accountStatus = accountStatus;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                user.getRole().name(),
                user.getFlatNumber(),
                user.getAccountStatus().name(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFlatNumber() { return flatNumber; }
    public String getAccountStatus() { return accountStatus; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
