package com.GraphicalPasswordAuth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.GraphicalPasswordAuth.model.User;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on the {@link User} entity.
 * 
 * Extends {@link JpaRepository} to provide built-in methods for persistence,
 * and defines custom queries for email and reset token lookups.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the user's email
     * @return an {@link Optional} containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their password reset token.
     *
     * @param resetToken the reset token
     * @return an {@link Optional} containing the user if the token is valid, or empty if not
     */
    Optional<User> findByResetToken(String resetToken);

}
