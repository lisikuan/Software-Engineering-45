package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for User entities and
 * hide JSON persistence details from services.
 */
public interface UserRepository {
    List<User> findAll() throws DataAccessException;

    Optional<User> findById(String id) throws DataAccessException;

    User insert(User user) throws DataAccessException;

    User update(User user) throws DataAccessException;

    boolean deleteById(String id) throws DataAccessException;
}
