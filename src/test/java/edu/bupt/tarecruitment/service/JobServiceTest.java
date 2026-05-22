package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.JobStatus;
import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.validation.JobValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void updateOwnJobUpdatesOnlyPublisherOwnedJob() throws Exception {
        JobService jobService = createJobService("""
                [
                  {
                    "id": "J001",
                    "title": "Java Programming TA",
                    "description": "Support tutorials",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6,
                    "quota": 1,
                    "status": "OPEN",
                    "publisherId": "U900"
                  }
                ]
                """);

        Job updated = jobService.updateOwnJob(
                "J001",
                "Advanced Java",
                List.of("Java", "Tutoring"),
                8,
                2,
                "Support advanced labs",
                "U900"
        );

        assertEquals("J001", updated.getId());
        assertEquals("Advanced Java TA", updated.getTitle());
        assertEquals("Advanced Java", updated.getCourseName());
        assertEquals(List.of("Java", "Tutoring"), updated.getRequiredSkills());
        assertEquals(8, updated.getWeeklyHours());
        assertEquals(2, updated.getQuota());
        assertEquals(JobStatus.OPEN, updated.getStatus());
        assertEquals("U900", updated.getPublisherId());
    }

    @Test
    void updateOwnJobRejectsDifferentPublisher() throws Exception {
        JobService jobService = createJobService("""
                [
                  {
                    "id": "J001",
                    "title": "Java Programming TA",
                    "description": "Support tutorials",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6,
                    "quota": 1,
                    "status": "OPEN",
                    "publisherId": "U900"
                  }
                ]
                """);

        assertThrows(BusinessException.class, () -> jobService.updateOwnJob(
                "J001",
                "Advanced Java",
                List.of("Java"),
                8,
                2,
                "Support labs",
                "U901"
        ));
    }

    @Test
    void deleteOwnJobDeletesOnlyPublisherOwnedJob() throws Exception {
        JobService jobService = createJobService("""
                [
                  {
                    "id": "J001",
                    "title": "Java Programming TA",
                    "description": "Support tutorials",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6,
                    "quota": 1,
                    "status": "OPEN",
                    "publisherId": "U900"
                  }
                ]
                """);

        jobService.deleteOwnJob("J001", "U900");

        assertFalse(jobService.getAllJobs().stream().anyMatch(job -> "J001".equals(job.getId())));
    }

    @Test
    void deleteOwnJobRejectsDifferentPublisher() throws Exception {
        JobService jobService = createJobService("""
                [
                  {
                    "id": "J001",
                    "title": "Java Programming TA",
                    "description": "Support tutorials",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java"],
                    "weeklyHours": 6,
                    "quota": 1,
                    "status": "OPEN",
                    "publisherId": "U900"
                  }
                ]
                """);

        assertThrows(BusinessException.class, () -> jobService.deleteOwnJob("J001", "U901"));
    }

    private JobService createJobService(String jobsJson) throws Exception {
        Files.writeString(tempDir.resolve("jobs.json"), jobsJson);
        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new JobService(new JsonJobRepository(jsonDataStore), new JobValidator());
    }
}
