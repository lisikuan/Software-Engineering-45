package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.persistence.repository.CvRepository;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;
import edu.bupt.tarecruitment.validation.StudentValidator;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StudentService {
    private final StudentRepository studentRepository;
    private final CvRepository cvRepository;
    private final StudentValidator studentValidator;

    public StudentService(
            StudentRepository studentRepository,
            CvRepository cvRepository,
            StudentValidator studentValidator
    ) {
        this.studentRepository = studentRepository;
        this.cvRepository = cvRepository;
        this.studentValidator = studentValidator;
    }

    public List<Student> getAllStudents() throws DataAccessException {
        return studentRepository.findAll();
    }

    public Optional<Student> findStudentByUserId(String userId) throws ValidationException, DataAccessException {
        if (userId == null || userId.isBlank()) {
            throw new ValidationException("User id must not be blank.");
        }

        return studentRepository.findAll().stream()
                .filter(student -> userId.equals(student.getUserId()))
                .findFirst();
    }

    public Student getStudentById(String studentId) throws ValidationException, BusinessException, DataAccessException {
        validateStudentId(studentId);
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Student not found for id: " + studentId));
    }

    public Student getStudentByUserId(String userId) throws ValidationException, BusinessException, DataAccessException {
        return findStudentByUserId(userId)
                .orElseThrow(() -> new BusinessException("Student profile not found for user id: " + userId));
    }

    public Student saveProfile(
            String userId,
            String name,
            String studentNumber,
            List<String> skillTags,
            Path cvSourceFile
    ) throws ValidationException, BusinessException, DataAccessException {
        studentValidator.validateProfileInput(name, studentNumber, skillTags, cvSourceFile);

        Optional<Student> existingStudent = findStudentByUserId(userId);
        ensureStudentNumberIsUnique(studentNumber, existingStudent.map(Student::getId).orElse(null));

        Student profile;
        if (existingStudent.isPresent()) {
            profile = existingStudent.get();
        } else {
            profile = new Student(nextStudentId(), name, userId);
        }
        profile.setName(name);
        profile.setStudentNumber(studentNumber);
        profile.setSkillTags(skillTags);
        if (cvSourceFile != null) {
            profile.setCvFilePath(cvRepository.storePdf(profile.getId(), cvSourceFile));
        }

        if (existingStudent.isPresent()) {
            return studentRepository.update(profile);
        }
        return studentRepository.insert(profile);
    }

    public Path resolveCvFilePath(String relativePath) throws DataAccessException {
        return cvRepository.resolveStoredFile(relativePath);
    }

    private void validateStudentId(String studentId) throws ValidationException {
        if (studentId == null || studentId.isBlank()) {
            throw new ValidationException("Student id must not be blank.");
        }
    }

    private void ensureStudentNumberIsUnique(String studentNumber, String currentStudentId)
            throws BusinessException, DataAccessException {
        boolean duplicated = studentRepository.findAll().stream()
                .anyMatch(student -> studentNumber.equals(student.getStudentNumber())
                        && !student.getId().equals(currentStudentId));
        if (duplicated) {
            throw new BusinessException("Student number already exists: " + studentNumber);
        }
    }

    private String nextStudentId() throws DataAccessException {
        int nextNumber = studentRepository.findAll().stream()
                .map(Student::getId)
                .map(this::extractNumericSuffix)
                .flatMap(Optional::stream)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
        return String.format("S%03d", nextNumber);
    }

    private Optional<Integer> extractNumericSuffix(String studentId) {
        if (studentId == null || studentId.length() < 2 || studentId.charAt(0) != 'S') {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(studentId.substring(1)));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
