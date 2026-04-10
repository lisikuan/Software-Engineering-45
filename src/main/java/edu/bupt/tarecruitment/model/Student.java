package edu.bupt.tarecruitment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsibility: represent the persisted Student profile entity used by the
 * current runnable version.
 *
 * Current baseline:
 * - id is the persistence key and internal student profile identifier.
 * - userId references User.id.
 * - studentNumber is the business student number entered by the user.
 * - skillTags stores zero or more student skill labels.
 * - cvFilePath stores the relative path of the copied PDF CV under data/cvs.
 * - [待确认] Whether persistence must also enforce userId uniqueness.
 */
public class Student {
    private String id;
    private String name;
    private String userId;
    private String studentNumber;
    private List<String> skillTags;
    private String cvFilePath;

    public Student() {
        this.skillTags = new ArrayList<>();
    }

    public Student(String id, String name, String userId) {
        this(id, name, userId, null, new ArrayList<>(), null);
    }

    public Student(String id, String name, String userId, String studentNumber, List<String> skillTags, String cvFilePath) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.studentNumber = studentNumber;
        this.skillTags = skillTags == null ? new ArrayList<>() : new ArrayList<>(skillTags);
        this.cvFilePath = cvFilePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public List<String> getSkillTags() {
        return skillTags;
    }

    public void setSkillTags(List<String> skillTags) {
        this.skillTags = skillTags == null ? new ArrayList<>() : new ArrayList<>(skillTags);
    }

    public String getCvFilePath() {
        return cvFilePath;
    }

    public void setCvFilePath(String cvFilePath) {
        this.cvFilePath = cvFilePath;
    }
}
