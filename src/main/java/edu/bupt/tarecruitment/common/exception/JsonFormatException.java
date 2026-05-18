package edu.bupt.tarecruitment.common.exception;

/**
 * Responsibility: represent malformed JSON content or JSON-to-model mapping
 * failures in the JSON persistence layer.
 */
public class JsonFormatException extends DataAccessException {
    public JsonFormatException(String message) {
        super(message);
    }

    public JsonFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
