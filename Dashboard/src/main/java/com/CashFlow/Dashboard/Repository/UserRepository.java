package com.CashFlow.Dashboard.Repository;

import com.CashFlow.Dashboard.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by username
    Optional<User> findByUsername(String username);

    // Find by email
    Optional<User> findByEmail(String email);

    // Check existence (useful for signup validation)
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
