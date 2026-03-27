package edu.bupt.tarecruitment.model;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String password;
    private UserRole role;
    private String email;
    private LocalDateTime createdAt;

    public User() {}

    public User(String id, String username, String password, UserRole role, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" + "id='" + id + "\'" + ", username='" + username + "\'" + ", role=" + role + ", email='" + email + "\'" + ", createdAt=" + createdAt + "}";
    }

    public enum UserRole {
        STUDENT, ADMIN, TA
    }
}