package edu.bupt.tarecruitment.model;

/**
 * Represents a single student's current TA workload.
 * Used by WorkloadService to produce an overview for Admin.
 */
public class WorkloadReport {
    private final String studentId;
    private final String studentName;
    private final int approvedJobCount;
    private final int totalWeeklyHours;
    private final boolean overloaded;

    /** Default maximum weekly hours before a student is flagged overloaded. */
    public static final int DEFAULT_MAX_WEEKLY_HOURS = 20;

    public WorkloadReport(String studentId, String studentName,
                          int approvedJobCount, int totalWeeklyHours,
                          boolean overloaded) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.approvedJobCount = approvedJobCount;
        this.totalWeeklyHours = totalWeeklyHours;
        this.overloaded = overloaded;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getApprovedJobCount() {
        return approvedJobCount;
    }

    public int getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public boolean isOverloaded() {
        return overloaded;
    }

    @Override
    public String toString() {
        return String.format("WorkloadReport{student=%s(%s), jobs=%d, hours=%d/week, overloaded=%s}",
                studentName, studentId, approvedJobCount, totalWeeklyHours, overloaded);
    }
}
