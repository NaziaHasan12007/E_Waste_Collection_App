package com.ewaste.server.domain.repository;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.model.user.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    /**
     * Save a user to the database (insert or update)
     * @param user The user to save
     * @return The saved user with generated ID
     */
    User save(User user);

    /**
     * Find a user by their ID
     * @param userId The user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(Long userId);

    /**
     * Find a user by their email address
     * @param email The email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users with a specific role
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(Role role);

    /**
     * Find all users
     * @return List of all users
     */
    List<User> findAll();

    /**
     * Delete a user by their ID
     * @param userId The user ID to delete
     */
    void deleteById(Long userId);

    /**
     * Check if a user exists by email
     * @param email The email to check
     * @return true if a user with the email exists
     */
    boolean existsByEmail(String email);
}