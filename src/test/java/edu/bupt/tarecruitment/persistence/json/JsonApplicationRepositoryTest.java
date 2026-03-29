package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.JsonFormatException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Scope of tests:
 * - verifies only the minimal JSON persistence behavior for Application.
 * - does not enforce the business rule that the same studentId should not
 *   apply to the same jobId twice; that rule is reserved for service layer.
 */
class JsonApplicationRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void findAllReturnsEmptyListWhenApplicationsFileIsEmptyArray() throws Exception {
        JsonApplicationRepository repository = createRepositoryWithApplicationsJson("[]");

        List<Application> applications = repository.findAll();

        assertTrue(applications.isEmpty());
    }

    @Test
    void insertWritesApplicationToApplicationsJson() throws Exception {
        JsonApplicationRepository repository = createRepositoryWithApplicationsJson("[]");
        Application application = createApplication("A001", "S001", "J001", ApplicationStatus.PENDING);

        repository.insert(application);

        String json = Files.readString(tempDir.resolve("applications.json"));
        assertTrue(json.contains("\"id\" : \"A001\""));
        assertTrue(json.contains("\"studentId\" : \"S001\""));
        assertTrue(json.contains("\"jobId\" : \"J001\""));
        assertTrue(json.contains("\"status\" : \"PENDING\""));
    }

    @Test
    void insertThrowsWhenApplicationIdAlreadyExists() throws Exception {
        JsonApplicationRepository repository = createRepositoryWithApplicationsJson("""
                [
                  {
                    "id": "A001",
                    "jobId": "J001",
                    "studentId": "S001",
                    "status": "PENDING",
                    "appliedAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> repository.insert(createApplication("A001", "S002", "J002", ApplicationStatus.PENDING))
        );

        assertTrue(exception.getMessage().contains("Duplicate id 'A001'"));
    }

    @Test
    void updateReplacesExistingApplicationInApplicationsJson() throws Exception {
        JsonApplicationRepository repository = createRepositoryWithApplicationsJson("""
                [
                  {
                    "id": "A001",
                    "jobId": "J001",
                    "studentId": "S001",
                    "status": "PENDING",
                    "appliedAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        Application updatedApp = createApplication("A001", "S009", "J008", ApplicationStatus.APPROVED);
        repository.update(updatedApp);

        Application storedApplication = repository.findById("A001").orElseThrow();
        assertEquals("S009", storedApplication.getStudentId());
        assertEquals("J008", storedApplication.getJobId());
        assertEquals(ApplicationStatus.APPROVED, storedApplication.getStatus());
    }

    @Test
    void deleteByIdReturnsTrueThenFalseForSameApplicationId() throws Exception {
        JsonApplicationRepository repository = createRepositoryWithApplicationsJson("""
                [
                  {
                    "id": "A001",
                    "jobId": "J001",
                    "studentId": "S001",
                    "status": "REJECTED",
                    "appliedAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        boolean firstDelete = repository.deleteById("A001");
        boolean secondDelete = repository.deleteById("A001");

        assertTrue(firstDelete);
        assertFalse(secondDelete);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void readThrowsJsonFormatExceptionWhenApplicationsJsonIsMalformed() throws Exception {
        JsonApplicationRepository repository = createRepositoryWithApplicationsJson("{ invalid json");

        assertThrows(JsonFormatException.class, repository::findAll);
    }

    private JsonApplicationRepository createRepositoryWithApplicationsJson(String jsonContent) throws Exception {
        Files.writeString(tempDir.resolve("applications.json"), jsonContent);
        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new JsonApplicationRepository(jsonDataStore);
    }

    private Application createApplication(String id, String studentId, String jobId, ApplicationStatus status) {
        return new Application(id, jobId, studentId, status, LocalDateTime.now(), null, null, null);
    }
}
