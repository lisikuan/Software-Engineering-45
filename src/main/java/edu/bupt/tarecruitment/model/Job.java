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
 * - [待确认] Whether later versions need additional job metadata.
 */
public class Job {
    private String id;
    private String title;
    private String description;
    private String courseName;
    private List<String> requiredSkills;
    private int weeklyHours;

    public Job() {
        this.requiredSkills = new ArrayList<>();
    }

    public Job(String id, String title, String description) {
        this(id, title, description, null, new ArrayList<>(), 0);
    }

    public Job(
            String id,
            String title,
            String description,
            String courseName,
            List<String> requiredSkills,
            int weeklyHours
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.courseName = courseName;
        this.requiredSkills = requiredSkills == null ? new ArrayList<>() : new ArrayList<>(requiredSkills);
        this.weeklyHours = weeklyHours;
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
}
