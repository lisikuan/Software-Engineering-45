package edu.bupt.tarecruitment.validation;

import edu.bupt.tarecruitment.common.exception.ValidationException;

import java.nio.file.Path;
import java.util.List;

public class StudentValidator {
    public void validateProfileInput(String name, String studentNumber, List<String> skillTags, Path cvSourceFile)
            throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Student name must not be blank.");
        }
        if (studentNumber == null || studentNumber.isBlank()) {
            throw new ValidationException("Student number must not be blank.");
        }
        if (skillTags == null) {
            throw new ValidationException("Skill tags must not be null.");
        }
        if (cvSourceFile != null && !cvSourceFile.getFileName().toString().toLowerCase().endsWith(".pdf")) {
            throw new ValidationException("CV file must be a PDF.");
        }
    }
}
