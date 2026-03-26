package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for Student entities
 * without exposing JSON or file-system details to upper layers.
 *
 * Current baseline:
 * - id means the student number/student identifier and is the repository key.
 * - userId references User.id.
 * - update must throw DataAccessException when the target id does not exist.
 * - interface naming remains unchanged for now.
 * - [待确认] Whether persistence must also enforce userId uniqueness.
 */
public interface StudentRepository {
    List<Student> findAll() throws DataAccessException;

    Optional<Student> findById(String id) throws DataAccessException;

    Student insert(Student student) throws DataAccessException;

    Student update(Student student) throws DataAccessException;

    boolean deleteById(String id) throws DataAccessException;
}