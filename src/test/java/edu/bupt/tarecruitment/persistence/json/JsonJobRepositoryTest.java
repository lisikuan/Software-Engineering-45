package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.JsonFormatException;
import edu.bupt.tarecruitment.model.Job;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
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
        Job job = new Job("J001", "Java Programming TA", "Assist with labs", "Java Programming", List.of("Java", "Communication"), 6);

        repository.insert(job);

        String json = Files.readString(tempDir.resolve("jobs.json"));
        assertTrue(json.contains("\"id\" : \"J001\""));
        assertTrue(json.contains("\"courseName\" : \"Java Programming\""));
        assertTrue(json.contains("\"weeklyHours\" : 6"));
    }

    @Test
    void insertThrowsWhenJobIdAlreadyExists() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("""
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "description": "Assist with labs",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6
                  }
                ]
                """);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> repository.insert(new Job("J001", "Database TA", "Assist with grading", "Database Systems", List.of("SQL"), 5))
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
                    "description": "Assist with labs",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6
                  }
                ]
                """);

        repository.update(new Job("J001", "Advanced Java TA", "Assist with lectures", "Advanced Java", List.of("Java", "Tutoring"), 8));

        Job storedJob = repository.findById("J001").orElseThrow();
        assertEquals("Advanced Java", storedJob.getCourseName());
        assertEquals(8, storedJob.getWeeklyHours());
        assertEquals(List.of("Java", "Tutoring"), storedJob.getRequiredSkills());
    }

    @Test
    void deleteByIdReturnsTrueThenFalseForSameJobId() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("""
                [
                  {
                    "id": "J001",
                    "title": "Java TA",
                    "description": "Assist with labs",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6
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
}
