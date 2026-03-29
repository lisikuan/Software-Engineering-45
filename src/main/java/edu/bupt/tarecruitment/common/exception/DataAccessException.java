package edu.bupt.tarecruitment.common.exception;

/**
 * Responsibility: represent failures in the persistence layer so
 * repository and JSON infrastructure can report file and storage problems
 * without using bare RuntimeException.
 */
public class DataAccessException extends RuntimeException {
    private final String filePath;
    private final String operation;

    public DataAccessException(String message) {
        super(message);
        this.filePath = null;
        this.operation = null;
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
        this.filePath = null;
        this.operation = null;
    }

    public DataAccessException(String message, String filePath, String operation) {
        super(message);
        this.filePath = filePath;
        this.operation = operation;
    }

    public DataAccessException(String message, String filePath, String operation, Throwable cause) {
        super(message, cause);
        this.filePath = filePath;
        this.operation = operation;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getOperation() {
        return operation;
    }
}
