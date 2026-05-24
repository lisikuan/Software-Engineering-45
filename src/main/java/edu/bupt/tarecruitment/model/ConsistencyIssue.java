package edu.bupt.tarecruitment.model;

/**
 * Responsibility: represent one read-only data consistency finding for the
 * Admin dashboard. The check reports issues only and does not repair data.
 */
public class ConsistencyIssue {
    private final String severity;
    private final String category;
    private final String message;

    public ConsistencyIssue(String severity, String category, String message) {
        this.severity = severity;
        this.category = category;
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }
}
