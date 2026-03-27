package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    List<User> findByRole(User.UserRole role);
    User update(User user);
    boolean delete(String id);
    boolean existsByUsername(String username);
    boolean existsById(String id);
    int count();
}