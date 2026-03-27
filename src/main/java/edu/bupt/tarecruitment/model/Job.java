package edu.bupt.tarecruitment.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Job {
    private String id;
    private String title;
    private String courseCode;
    private String description;
    private List<String> requiredSkills;
    private int maxApplications;
    private JobStatus status;
    private LocalDateTime createdAt;

    public Job() { this.requiredSkills = new ArrayList<>(); }

    public Job(String id, String title, String courseCode, String description,
               List<String> requiredSkills, int maxApplications, JobStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.courseCode = courseCode;
        this.description = description;
        this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
        this.maxApplications = maxApplications;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getRequiredSkills() { return new ArrayList<>(requiredSkills); }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>(); }
    public int getMaxApplications() { return maxApplications; }
    public void setMaxApplications(int maxApplications) { this.maxApplications = maxApplications; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Job{" + "id='" + id + "\'" + ", title='" + title + "\'" + ", courseCode='" + courseCode + "\'" + ", status=" + status + "}";
    }

    public enum JobStatus { OPEN, CLOSED, FILLED }
}