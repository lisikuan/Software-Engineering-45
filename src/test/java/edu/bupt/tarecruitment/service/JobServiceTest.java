package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.validation.JobValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void publishJobCreatesJobWithGeneratedId() throws Exception {
        JobService jobService = createJobService("""
                [
                  {
                    "id": "J001",
                    "title": "Java Programming TA",
                    "description": "Support tutorials",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6
                  }
                ]
                """);

        Job saved = jobService.publishJob(
                "Software Testing",
                List.of("JUnit", "Communication"),
                4,
                1,
                "Assist lab exercises",
                "U900"
        );

        assertEquals("J002", saved.getId());
        assertEquals("Software Testing TA", saved.getTitle());
        assertEquals("Software Testing", saved.getCourseName());
        assertEquals(List.of("JUnit", "Communication"), saved.getRequiredSkills());
        assertEquals(4, saved.getWeeklyHours());
        assertEquals(1, saved.getQuota());
    }

    private JobService createJobService(String jobsJson) throws Exception {
        Files.writeString(tempDir.resolve("jobs.json"), jobsJson);
        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new JobService(new JsonJobRepository(jsonDataStore), new JobValidator());
    }
}
