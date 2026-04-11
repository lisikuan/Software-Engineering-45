package edu.bupt.tarecruitment.validation;

import edu.bupt.tarecruitment.common.exception.ValidationException;

import java.util.List;

public class JobValidator {
    public void validateJobInput(String courseName, List<String> requiredSkills, int weeklyHours) throws ValidationException {
        if (courseName == null || courseName.isBlank()) {
            throw new ValidationException("Course name must not be blank.");
        }
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new ValidationException("Required skills must not be empty.");
        }
        if (weeklyHours <= 0) {
            throw new ValidationException("Weekly hours must be a positive integer.");
        }
    }
}
