package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.JsonFormatException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.JobStatus;
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

class JsonJobRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void findAllReturnsEmptyListWhenJobsFileIsEmptyArray() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("[]");

        List<Job> jobs = repository.findAll();

        assertTrue(jobs.isEmpty());
    }

    @Test
    void insertWritesJobToJobsJson() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("[]");
        Job job = createJob("J001", "Java TA", "CS101", "Assist with labs");

        repository.insert(job);

        String json = Files.readString(tempDir.resolve("jobs.json"));
        assertTrue(json.contains("\"id\" : \"J001\""));
        assertTrue(json.contains("\"title\" : \"Java TA\""));
        assertTrue(json.contains("\"description\" : \"Assist with labs\""));
    }

    @Test
    void insertThrowsWhenJobIdAlreadyExists() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("""
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "courseCode": "CS101",
                    "description": "Assist with labs",
                    "requiredSkills": [],
                    "maxApplications": 10,
                    "status": "OPEN",
                    "createdAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> repository.insert(createJob("J001", "Database TA", "CS102", "Assist with grading"))
        );

        assertTrue(exception.getMessage().contains("Duplicate id 'J001'"));
    }

    @Test
    void updateReplacesExistingJobInJobsJson() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("""
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "courseCode": "CS101",
                    "description": "Assist with labs",
                    "requiredSkills": [],
                    "maxApplications": 10,
                    "status": "OPEN",
                    "createdAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        Job updatedJob = createJob("J001", "Advanced Java TA", "CS101", "Assist with lectures");
        repository.update(updatedJob);

        Job storedJob = repository.findById("J001").orElseThrow();
        assertEquals("Advanced Java TA", storedJob.getTitle());
        assertEquals("Assist with lectures", storedJob.getDescription());
    }

    @Test
    void deleteByIdReturnsTrueThenFalseForSameJobId() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("""
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "courseCode": "CS101",
                    "description": "Assist with labs",
                    "requiredSkills": [],
                    "maxApplications": 10,
                    "status": "OPEN",
                    "createdAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        boolean firstDelete = repository.deleteById("J001");
        boolean secondDelete = repository.deleteById("J001");

        assertTrue(firstDelete);
        assertFalse(secondDelete);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void readThrowsJsonFormatExceptionWhenJobsJsonIsMalformed() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("{ invalid json");

        assertThrows(JsonFormatException.class, repository::findAll);
    }

    private JsonJobRepository createRepositoryWithJobsJson(String jsonContent) throws Exception {
        Files.writeString(tempDir.resolve("jobs.json"), jsonContent);
        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new JsonJobRepository(jsonDataStore);
    }

    private Job createJob(String id, String title, String courseCode, String description) {
        return new Job(id, title, courseCode, description, List.of(), 10, JobStatus.OPEN, LocalDateTime.now());
    }
}
