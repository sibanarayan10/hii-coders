package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.User;
import com.sibanarayan.code.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>  {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRole(String email, UserRole role);
}
