package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.ConsistencyIssue;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import edu.bupt.tarecruitment.persistence.json.JsonApplicationRepository;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.persistence.json.JsonStudentRepository;
import edu.bupt.tarecruitment.persistence.json.JsonUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void createUserCreatesTaAccountWithGeneratedId() throws Exception {
        AdminService adminService = createAdminService(validUsersJson(), validStudentsJson(), validJobsJson(), validApplicationsJson());

        User user = adminService.createUser("ta2", "ta234", UserRole.TA);

        assertEquals("U1000", user.getId());
        assertEquals("ta2", user.getUsername());
        assertEquals(UserRole.TA, user.getRole());
    }

    @Test
    void createUserCreatesMoAccountWithGeneratedId() throws Exception {
        AdminService adminService = createAdminService(validUsersJson(), validStudentsJson(), validJobsJson(), validApplicationsJson());

        User user = adminService.createUser("mo2", "mo234", UserRole.MO);

        assertEquals("U1000", user.getId());
        assertEquals("mo2", user.getUsername());
        assertEquals(UserRole.MO, user.getRole());
    }

    @Test
    void createUserRejectsDuplicateUsername() throws Exception {
        AdminService adminService = createAdminService(validUsersJson(), validStudentsJson(), validJobsJson(), validApplicationsJson());

        assertThrows(BusinessException.class, () -> adminService.createUser("ta1", "newpass", UserRole.TA));
    }

    @Test
    void createUserRejectsAdminRole() throws Exception {
        AdminService adminService = createAdminService(validUsersJson(), validStudentsJson(), validJobsJson(), validApplicationsJson());

        assertThrows(ValidationException.class, () -> adminService.createUser("admin2", "admin234", UserRole.ADMIN));
    }

    @Test
    void consistencyCheckReportsMissingReferences() throws Exception {
        AdminService adminService = createAdminService(
                validUsersJson(),
                """
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U404"
                  }
                ]
                """,
                """
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "description": "Support tutorials",
                    "courseName": "Java",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6,
                    "quota": 1,
                    "status": "OPEN",
                    "publisherId": "U404"
                  }
                ]
                """,
                """
                [
                  {
                    "id": "A001",
                    "studentId": "S404",
                    "jobId": "J404",
                    "status": "SUBMITTED"
                  }
                ]
                """
        );

        List<ConsistencyIssue> issues = adminService.getConsistencyIssues();

        assertTrue(issues.stream().anyMatch(issue -> issue.getMessage().contains("missing userId U404")));
        assertTrue(issues.stream().anyMatch(issue -> issue.getMessage().contains("missing publisherId U404")));
        assertTrue(issues.stream().anyMatch(issue -> issue.getMessage().contains("missing studentId S404")));
        assertTrue(issues.stream().anyMatch(issue -> issue.getMessage().contains("missing jobId J404")));
    }

    private AdminService createAdminService(
            String usersJson,
            String studentsJson,
            String jobsJson,
            String applicationsJson
    ) throws Exception {
        Files.writeString(tempDir.resolve("users.json"), usersJson);
        Files.writeString(tempDir.resolve("students.json"), studentsJson);
        Files.writeString(tempDir.resolve("jobs.json"), jobsJson);
        Files.writeString(tempDir.resolve("applications.json"), applicationsJson);

        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new AdminService(
                new JsonUserRepository(jsonDataStore),
                new JsonStudentRepository(jsonDataStore),
                new JsonJobRepository(jsonDataStore),
                new JsonApplicationRepository(jsonDataStore)
        );
    }

    private String validUsersJson() {
        return """
                [
                  {
                    "id": "U001",
                    "username": "ta1",
                    "password": "ta123",
                    "role": "TA"
                  },
                  {
                    "id": "U900",
                    "username": "mo1",
                    "password": "mo123",
                    "role": "MO"
                  },
                  {
                    "id": "U999",
                    "username": "admin1",
                    "password": "admin123",
                    "role": "ADMIN"
                  }
                ]
                """;
    }

    private String validStudentsJson() {
        return """
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001"
                  }
                ]
                """;
    }

    private String validJobsJson() {
        return """
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "description": "Support tutorials",
                    "courseName": "Java",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6,
                    "quota": 1,
                    "status": "OPEN",
                    "publisherId": "U900"
                  }
                ]
                """;
    }

    private String validApplicationsJson() {
        return """
                [
                  {
                    "id": "A001",
                    "studentId": "S001",
                    "jobId": "J001",
                    "status": "APPROVED"
                  }
                ]
                """;
    }
}
