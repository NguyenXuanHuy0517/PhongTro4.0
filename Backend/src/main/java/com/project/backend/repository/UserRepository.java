package com.project.backend.repository;

import com.project.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByRole_RoleName(String roleName);
    long countByRole_RoleName(String roleName);
    List<User> findByRole_RoleNameAndIsActiveFalse(String roleName);
    long countByRole_RoleNameAndIsActiveFalse(String roleName);
    Optional<User> findByResetTokenAndResetTokenExpiryAfter(String resetToken, LocalDateTime now);
}
