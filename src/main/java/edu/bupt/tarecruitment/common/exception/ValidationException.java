package edu.bupt.tarecruitment.common.exception;

public class ValidationException extends RuntimeException {
    private String field;
    private String value;

    public ValidationException(String message) { super(message); }
    public ValidationException(String message, Throwable cause) { super(message, cause); }
    public ValidationException(String message, String field, String value) {
        super(String.format("Validation failed for field '%s': %s", field, message));
        this.field = field;
        this.value = value;
    }

    public String getField() { return field; }
    public String getValue() { return value; }
}