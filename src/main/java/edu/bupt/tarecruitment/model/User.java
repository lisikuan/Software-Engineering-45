package edu.bupt.tarecruitment.model;

/**
 * Responsibility: represent the minimal persisted User entity for login and
 * role-based navigation in the runnable test version.
 *
 * Current baseline:
 * - id is the persistence key.
 * - username is the login name shown in the UI.
 * - password is stored as plain text only for the current runnable test version.
 * - role controls whether the UI opens the student or admin flow.
 * - [待确认] A safer password storage strategy for later iterations.
 */
public class User {
    private String id;
    private String username;
    private String password;
    private UserRole role;

    public User() {
    }

    public User(String id, String username, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
