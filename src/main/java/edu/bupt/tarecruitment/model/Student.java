package edu.bupt.tarecruitment.model;

/**
 * Responsibility: represent the minimal persisted Student entity.
 * Current baseline:
 * - id means the student number/student identifier and is the persistence key.
 * - userId references User.id.
 * - the current minimal field set is id, name, userId only.
 * - [待确认] Whether persistence must also enforce userId uniqueness.
 */
public class Student {
    private String id;
    private String name;
    private String userId;

    public Student() {
    }

    public Student(String id, String name, String userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
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
}