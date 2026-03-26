package edu.bupt.tarecruitment.model;

/**
 * Responsibility: represent the minimal persisted Job entity.
 * Current baseline:
 * - id is the persistence key for Job.
 * - the current minimal field set is id, title, description only.
 * - [待确认] Whether job id maps to a course code, posting id, or another
 *   business identifier.
 * - [待确认] Additional fields such as course, teacher, quota, deadline,
 *   publisher, and status.
 */
public class Job {
    private String id;
    private String title;
    private String description;

    public Job() {
    }

    public Job(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
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
}