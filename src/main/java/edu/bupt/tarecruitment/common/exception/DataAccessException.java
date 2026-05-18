package edu.bupt.tarecruitment.common.exception;

/**
 * Responsibility: represent checked failures in the persistence layer so
 * repository and JSON infrastructure can report file and storage problems
 * without using bare RuntimeException.
 */
public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
