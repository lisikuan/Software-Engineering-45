package edu.bupt.tarecruitment.common.exception;

public class DataAccessException extends RuntimeException {
    private String resourcePath;
    private String operation;

    public DataAccessException(String message) { super(message); }
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
    public DataAccessException(String message, String resourcePath, String operation) {
        super(String.format("[%s] %s: %s", operation, resourcePath, message));
        this.resourcePath = resourcePath;
        this.operation = operation;
    }
    public DataAccessException(String message, String resourcePath, String operation, Throwable cause) {
        super(String.format("[%s] %s: %s", operation, resourcePath, message), cause);
        this.resourcePath = resourcePath;
        this.operation = operation;
    }

    public String getResourcePath() { return resourcePath; }
    public String getOperation() { return operation; }
}