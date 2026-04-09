package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;

import java.util.List;

public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() throws DataAccessException {
        return studentRepository.findAll();
    }

    public Student getStudentById(String studentId) throws ValidationException, BusinessException, DataAccessException {
        validateStudentId(studentId);
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Student not found for id: " + studentId));
    }

    public Student getStudentByUserId(String userId) throws ValidationException, BusinessException, DataAccessException {
        if (userId == null || userId.isBlank()) {
            throw new ValidationException("User id must not be blank.");
        }

        return studentRepository.findAll().stream()
                .filter(student -> userId.equals(student.getUserId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Student profile not found for user id: " + userId));
    }

    private void validateStudentId(String studentId) throws ValidationException {
        if (studentId == null || studentId.isBlank()) {
            throw new ValidationException("Student id must not be blank.");
        }
    }
}
