package edu.bupt.tarecruitment.validation;

import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.common.exception.ValidationException;

public class ApplicationValidator {
    public void validate(Application application) throws ValidationException {
        if (application == null) {
            throw new ValidationException("Application must not be null.");
        }
        if (application.getId() == null || application.getId().isBlank()) {
            throw new ValidationException("Application id must not be blank.");
        }
    }
}
