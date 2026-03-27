package edu.bupt.tarecruitment.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String studentId;
    private String email;
    private String phone;
    private String cvPath;
    private List<String> skills;
    private LocalDateTime createdAt;

    public Student() { this.skills = new ArrayList<>(); }

    public Student(String id, String userId, String firstName, String lastName, String studentId,
                   String email, String phone, String cvPath, List<String> skills, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.email = email;
        this.phone = phone;
        this.cvPath = cvPath;
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }
    public List<String> getSkills() { return new ArrayList<>(skills); }
    public void setSkills(List<String> skills) { this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Student{" + "id='" + id + "\'" + ", firstName='" + firstName + "\'" + ", lastName='" + lastName + "\'" + ", studentId='" + studentId + "\'" + ", skills=" + skills + "}";
    }
}