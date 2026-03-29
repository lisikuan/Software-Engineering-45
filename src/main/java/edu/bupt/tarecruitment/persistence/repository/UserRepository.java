package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for User entities and
 * hide JSON persistence details from services.
 */
public interface UserRepository {
    List<User> findAll();

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    List<User> findByRole(UserRole role);

    User insert(User user);

    User update(User user);

    boolean deleteById(String id);

    boolean existsByUsername(String username);

    boolean existsById(String id);

    int count();
}
