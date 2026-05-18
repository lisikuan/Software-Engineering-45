package edu.bupt.tarecruitment.validation;

import java.util.List;

import edu.bupt.tarecruitment.common.exception.ValidationException;

public class JobValidator {
    public void validateJobInput(String courseName, List<String> requiredSkills, int weeklyHours, int quota) throws ValidationException {
        if (courseName == null || courseName.isBlank()) {
            throw new ValidationException("Course name must not be blank.");
        }
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new ValidationException("Required skills must not be empty.");
        }
        if (weeklyHours <= 0) {
            throw new ValidationException("Weekly hours must be a positive integer.");
        }
        if (quota <= 0) {
            throw new ValidationException("Quota must be a positive integer.");
        }
    }
}
