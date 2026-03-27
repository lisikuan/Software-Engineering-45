package edu.bupt.tarecruitment.common;

import java.util.regex.Pattern;

import edu.bupt.tarecruitment.common.exception.ValidationException;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,15}$");

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Field cannot be empty or blank", fieldName, value);
        }
    }

    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        validateNotBlank(value, fieldName);
        if (value.length() < minLength || value.length() > maxLength) {
            throw new ValidationException(String.format("Length must be between %d and %d", minLength, maxLength), fieldName, value);
        }
    }

    public static void validateEmail(String email) {
        validateNotBlank(email, "email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format", "email", email);
        }
    }

    public static void validateUsername(String username) {
        validateNotBlank(username, "username");
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("Username must be 3-20 characters and contain only letters, numbers, and underscores", "username", username);
        }
    }

    public static void validatePassword(String password) {
        validateNotBlank(password, "password");
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long", "password", "");
        }
    }

    public static void validatePhone(String phone) {
        validateNotBlank(phone, "phone");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Phone must contain 10-15 digits", "phone", phone);
        }
    }

    public static void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new ValidationException("Field cannot be null", fieldName, null);
        }
    }

    public static void validateId(String id) {
        validateNotBlank(id, "id");
    }
}