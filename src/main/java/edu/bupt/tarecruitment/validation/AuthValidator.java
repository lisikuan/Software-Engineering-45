package edu.bupt.tarecruitment.validation;

import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.UserRole;

public class AuthValidator {
    public void validateLoginInput(String username, String password, UserRole role) throws ValidationException {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Username must not be blank.");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("Password must not be blank.");
        }
        if (role == null) {
            throw new ValidationException("User role must not be null.");
        }
    }
}
