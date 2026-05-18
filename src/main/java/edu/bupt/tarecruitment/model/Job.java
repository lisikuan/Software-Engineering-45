package edu.bupt.tarecruitment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsibility: represent the persisted Job entity used by the current
 * runnable version.
 *
 * Current baseline:
 * - id is the persistence key for Job.
 * - title remains a display field for the current UI.
 * - courseName stores the course entered by admin during job posting.
 * - requiredSkills stores the skill labels expected by the job.
 * - weeklyHours stores the expected weekly workload.
 * - status tracks whether the job is open or closed.
 * - quota stores the maximum number of approved students.
 * - publisherId references User.id of the MO who published the job.
 */
public class Job {
    private String id;
    private String title;
    private String description;
    private String courseName;
    private List<String> requiredSkills;
    private int weeklyHours;
    private int quota;
    private JobStatus status;
    private String publisherId;

    public Job() {
        this.requiredSkills = new ArrayList<>();
        this.status = JobStatus.OPEN;
        this.publisherId = "";
    }

    public Job(String id, String title, String description) {
        this(id, title, description, null, new ArrayList<>(), 0, 1, JobStatus.OPEN, "");
    }

    public Job(
            String id,
            String title,
            String description,
            String courseName,
            List<String> requiredSkills,
            int weeklyHours,
            int quota,
            JobStatus status,
            String publisherId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.courseName = courseName;
        this.requiredSkills = requiredSkills == null ? new ArrayList<>() : new ArrayList<>(requiredSkills);
        this.weeklyHours = weeklyHours;
        this.quota = quota;
        this.status = status == null ? JobStatus.OPEN : status;
        this.publisherId = publisherId == null ? "" : publisherId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills == null ? new ArrayList<>() : new ArrayList<>(requiredSkills);
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }
}
