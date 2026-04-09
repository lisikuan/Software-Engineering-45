package edu.bupt.tarecruitment.validation;

import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.ApplicationStatus;

public class ApplicationValidator {
    public void validateSubmission(String studentId, String jobId) throws ValidationException {
        if (studentId == null || studentId.isBlank()) {
            throw new ValidationException("Student id must not be blank.");
        }
        if (jobId == null || jobId.isBlank()) {
            throw new ValidationException("Job id must not be blank.");
        }
    }

    public void validateStatusUpdate(String applicationId, ApplicationStatus newStatus) throws ValidationException {
        if (applicationId == null || applicationId.isBlank()) {
            throw new ValidationException("Application id must not be blank.");
        }
        if (newStatus == null) {
            throw new ValidationException("Application status must not be null.");
        }
    }
}
