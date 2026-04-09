package edu.bupt.tarecruitment.controller;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.service.StudentService;

import java.util.List;

public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public List<Student> getAllStudents() throws DataAccessException {
        return studentService.getAllStudents();
    }

    public Student getStudentById(String studentId) throws ValidationException, BusinessException, DataAccessException {
        return studentService.getStudentById(studentId);
    }

    public Student getStudentByUserId(String userId) throws ValidationException, BusinessException, DataAccessException {
        return studentService.getStudentByUserId(userId);
    }
}
