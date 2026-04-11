package edu.bupt.tarecruitment.controller;
<<<<<<< HEAD
=======

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.service.StudentService;
>>>>>>> 3671f8efbaf0da2baeaf2e256d12a477dd7b649b

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.service.StudentService;

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

    public Optional<Student> findStudentByUserId(String userId) throws ValidationException, DataAccessException {
        return studentService.findStudentByUserId(userId);
    }

    public Student getStudentByUserId(String userId) throws ValidationException, BusinessException, DataAccessException {
        return studentService.getStudentByUserId(userId);
    }

    public Student saveProfile(String userId,
                               String name,
                               String studentNumber,
                               String major,
                               String grade,
                               List<String> skillTags,
                               Path cvSourceFile)
            throws ValidationException, BusinessException, DataAccessException {
        return studentService.saveProfile(userId, name, studentNumber, major, grade, skillTags, cvSourceFile);
    }

    public List<String> getMissingSkills(String studentId, Job job)
            throws ValidationException, DataAccessException, BusinessException {
        return studentService.getMissingSkills(studentId, job);
    }

    public Path resolveCvFilePath(String relativePath) throws DataAccessException {
        return studentService.resolveCvFilePath(relativePath);
    }
}
