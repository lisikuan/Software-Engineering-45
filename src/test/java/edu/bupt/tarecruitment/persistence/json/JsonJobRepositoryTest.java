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
        Job job = new Job("J001", "Java TA", "Assist with labs");

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
                    \"id\": \"J001\",
                    \"title\": \"Java TA\",
                    \"description\": \"Assist with labs\"
                  }
                ]
                """);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> repository.insert(new Job("J001", "Database TA", "Assist with grading"))
        );

        assertTrue(exception.getMessage().contains("Duplicate id 'J001'"));
    }

    @Test
    void updateReplacesExistingJobInJobsJson() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("""
                [
                  {
                    \"id\": \"J001\",
                    \"title\": \"Java TA\",
                    \"description\": \"Assist with labs\"
                  }
                ]
                """);

        repository.update(new Job("J001", "Advanced Java TA", "Assist with lectures"));

        Job storedJob = repository.findById("J001").orElseThrow();
        assertEquals("Advanced Java TA", storedJob.getTitle());
        assertEquals("Assist with lectures", storedJob.getDescription());
    }

    @Test
    void deleteByIdReturnsTrueThenFalseForSameJobId() throws Exception {
        JsonJobRepository repository = createRepositoryWithJobsJson("""
                [
                  {
                    \"id\": \"J001\",
                    \"title\": \"Java TA\",
                    \"description\": \"Assist with labs\"
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