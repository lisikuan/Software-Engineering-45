package edu.bupt.tarecruitment.model;

/**
 * Responsibility: represent the minimal persisted Application entity.
 * Current baseline:
 * - id means the internal application record identifier and is the persistence key.
 * - studentId references Student.id.
 * - jobId references Job.id, where Job.id means the job/post identifier.
 * - studentId and jobId are business association fields, not a composite key.
 * - the current minimal field set is id, studentId, jobId, status only.
 * - [待确认] Additional fields such as submittedAt, reviewedAt, and comments.
 */
public class Application {
    private String id;
    private String studentId;
    private String jobId;
    private ApplicationStatus status;

    public Application() {
    }

    public Application(String id, String studentId, String jobId, ApplicationStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}