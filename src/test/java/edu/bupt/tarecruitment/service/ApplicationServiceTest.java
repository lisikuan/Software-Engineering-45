package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import edu.bupt.tarecruitment.persistence.json.JsonApplicationRepository;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.persistence.json.JsonStudentRepository;
import edu.bupt.tarecruitment.validation.ApplicationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void submitApplicationWritesNewApplication() throws Exception {
        ApplicationService applicationService = createApplicationService("[]");

        Application application = applicationService.submitApplication("S001", "J001");

        assertEquals("A001", application.getId());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        List<Application> applications = applicationService.getAllApplications();
        assertEquals(1, applications.size());
    }

    @Test
    void submitApplicationThrowsWhenSameStudentAppliesTwice() throws Exception {
        ApplicationService applicationService = createApplicationService("""
                [
                  {
                    "id": "A001",
                    "studentId": "S001",
                    "jobId": "J001",
                    "status": "SUBMITTED"
                  }
                ]
                """);

        assertThrows(BusinessException.class, () -> applicationService.submitApplication("S001", "J001"));
    }

    @Test
    void submitApplicationThrowsWhenStudentDoesNotExist() throws Exception {
        ApplicationService applicationService = createApplicationService("[]");

        assertThrows(BusinessException.class, () -> applicationService.submitApplication("S999", "J001"));
    }

    @Test
    void updateApplicationStatusApprovesSubmittedApplication() throws Exception {
        ApplicationService applicationService = createApplicationService("""
                [
                  {
                    "id": "A001",
                    "studentId": "S001",
                    "jobId": "J001",
                    "status": "SUBMITTED"
                  }
                ]
                """);

        Application updated = applicationService.updateApplicationStatus("A001", ApplicationStatus.APPROVED);

        assertEquals(ApplicationStatus.APPROVED, updated.getStatus());
    }

    @Test
    void updateApplicationStatusThrowsForIllegalSecondReview() throws Exception {
        ApplicationService applicationService = createApplicationService("""
                [
                  {
                    "id": "A001",
                    "studentId": "S001",
                    "jobId": "J001",
                    "status": "APPROVED"
                  }
                ]
                """);

        assertThrows(
                BusinessException.class,
                () -> applicationService.updateApplicationStatus("A001", ApplicationStatus.REJECTED)
        );
    }

    private ApplicationService createApplicationService(String applicationsJson) throws Exception {
        Files.writeString(tempDir.resolve("students.json"), """
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001"
                  }
                ]
                """);
        Files.writeString(tempDir.resolve("jobs.json"), """
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "description": "Assist with labs"
                  },
                  {
                    "id": "J002",
                    "title": "Database TA",
                    "description": "Assist with grading"
                  }
                ]
                """);
        Files.writeString(tempDir.resolve("applications.json"), applicationsJson);

        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new ApplicationService(
                new JsonApplicationRepository(jsonDataStore),
                new JsonStudentRepository(jsonDataStore),
                new JsonJobRepository(jsonDataStore),
                new ApplicationValidator()
        );
    }
}
