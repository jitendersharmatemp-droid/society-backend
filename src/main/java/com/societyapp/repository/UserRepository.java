package com.societyapp.repository;

import com.societyapp.entity.AccountStatus;
import com.societyapp.entity.Role;
import com.societyapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByFlatNumber(String flatNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    List<User> findByAccountStatus(AccountStatus status);
    List<User> findByRole(Role role);
    List<User> findByRoleAndAccountStatus(Role role, AccountStatus status);
}
