package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Student create(Student student);
    Optional<Student> findById(String id);
    Optional<Student> findByUserId(String userId);
    Optional<Student> findByStudentId(String studentId);
    List<Student> findAll();
    Student update(Student student);
    boolean delete(String id);
    boolean existsByStudentId(String studentId);
    boolean existsById(String id);
    int count();
}