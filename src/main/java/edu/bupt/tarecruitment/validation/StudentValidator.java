package edu.bupt.tarecruitment.validation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.bupt.tarecruitment.common.exception.ValidationException;

public class StudentValidator {
    public void validateProfileInput(String name,
                                     String studentNumber,
                                     String major,
                                     String grade,
                                     List<String> skillTags,
                                     Path cvSourceFile)
            throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Student name must not be blank.");
        }
        if (studentNumber == null || studentNumber.isBlank()) {
            throw new ValidationException("Student number must not be blank.");
        }
        if (major == null || major.isBlank()) {
            throw new ValidationException("Major must not be blank.");
        }
        if (grade == null || grade.isBlank()) {
            throw new ValidationException("Grade must not be blank.");
        }
        if (skillTags == null) {
            throw new ValidationException("Skill tags must not be null.");
        }
        if (cvSourceFile != null) {
            if (!Files.exists(cvSourceFile) || !Files.isRegularFile(cvSourceFile)) {
                throw new ValidationException("CV file path must point to an existing file.");
            }
            if (!cvSourceFile.getFileName().toString().toLowerCase().endsWith(".pdf")) {
                throw new ValidationException("CV file must be a PDF.");
            }
        }
    }
}
