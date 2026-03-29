package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for Student entities
 * without exposing JSON or file-system details to upper layers.
 */
public interface StudentRepository {
    List<Student> findAll();

    Optional<Student> findById(String id);

    Student insert(Student student);

    Student update(Student student);

    boolean deleteById(String id);
}
