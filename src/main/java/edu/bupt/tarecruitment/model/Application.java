package edu.bupt.tarecruitment.model;

import java.time.LocalDateTime;

public class Application {
    private String id;
    private String jobId;
    private String studentId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String reviewComment;

    public Application() {}

    public Application(String id, String jobId, String studentId, ApplicationStatus status,
                       LocalDateTime appliedAt, LocalDateTime reviewedAt, String reviewedBy, String reviewComment) {
        this.id = id;
        this.jobId = jobId;
        this.studentId = studentId;
        this.status = status;
        this.appliedAt = appliedAt;
        this.reviewedAt = reviewedAt;
        this.reviewedBy = reviewedBy;
        this.reviewComment = reviewComment;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    @Override
    public String toString() {
        return "Application{" + "id='" + id + "\'" + ", jobId='" + jobId + "\'" + ", studentId='" + studentId + "\'" + ", status=" + status + ", appliedAt=" + appliedAt + "}";
    }

    public enum ApplicationStatus { PENDING, APPROVED, REJECTED, WITHDRAWN }
}