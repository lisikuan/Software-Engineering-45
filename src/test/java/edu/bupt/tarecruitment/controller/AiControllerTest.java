package edu.bupt.tarecruitment.controller;

import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.JobStatus;
import edu.bupt.tarecruitment.model.MatchResult;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.WorkloadReport;
import edu.bupt.tarecruitment.persistence.json.JsonApplicationRepository;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.persistence.json.JsonStudentRepository;
import edu.bupt.tarecruitment.service.AiMatchingService;
import edu.bupt.tarecruitment.service.SkillNormalizer;
import edu.bupt.tarecruitment.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiControllerTest {

    @TempDir
    Path tempDir;

    private AiController aiController;

    @BeforeEach
    void setUp() throws Exception {
        Files.writeString(tempDir.resolve("students.json"), """
                [
                  {
                    "id": "S001", "name": "Alice", "userId": "U001",
                    "studentNumber": "2024001", "major": "CS", "grade": "3",
                    "skillTags": ["Java", "Communication"], "cvFilePath": null
                  },
                  {
                    "id": "S002", "name": "Bob", "userId": "U002",
                    "studentNumber": "2024002", "major": "CS", "grade": "3",
                    "skillTags": ["Python", "SQL"], "cvFilePath": null
                  }
                ]
                """);
        Files.writeString(tempDir.resolve("jobs.json"), """
                [
                  {
                    "id": "J001", "title": "Java TA", "description": "Labs",
                    "courseName": "Java", "requiredSkills": ["Java", "Communication"],
                    "weeklyHours": 6, "quota": 2, "status": "OPEN", "publisherId": "U900"
                  }
                ]
                """);
        Files.writeString(tempDir.resolve("applications.json"), """
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" }
                ]
                """);

        JsonDataStore store = new JsonDataStore(tempDir);
        JsonStudentRepository studentRepo = new JsonStudentRepository(store);
        JsonJobRepository jobRepo = new JsonJobRepository(store);
        JsonApplicationRepository appRepo = new JsonApplicationRepository(store);

        AiMatchingService ai = new AiMatchingService(studentRepo, jobRepo, new SkillNormalizer());
        WorkloadService workload = new WorkloadService(studentRepo, jobRepo, appRepo, ai);
        aiController = new AiController(ai, workload);
    }

    @Test
    void matchReturnsScoredResult() {
        Student s = new Student("S001", "Alice", "U001", "2024001", "CS", "3",
                List.of("Java", "Communication"), null);
        Job j = new Job("J001", "Java TA", "Labs", "Java",
                List.of("Java", "Communication"), 6, 2, JobStatus.OPEN, "U900");

        MatchResult result = aiController.match(s, j);

        assertNotNull(result);
        assertEquals(1.0, result.getScore(), 0.001);
    }

    @Test
    void rankApplicantsForJobReturnsRankedList() throws Exception {
        List<MatchResult> ranked = aiController.rankApplicantsForJob("J001");

        assertFalse(ranked.isEmpty());
        assertEquals("S001", ranked.get(0).getStudentId());
    }

    @Test
    void rankJobsForStudentReturnsResults() throws Exception {
        List<MatchResult> ranked = aiController.rankJobsForStudent("S001");

        assertFalse(ranked.isEmpty());
    }

    @Test
    void getMissingSkillsIdentifiesGaps() {
        Student bob = new Student("S002", "Bob", "U002", "2024002", "CS", "3",
                List.of("Python", "SQL"), null);
        Job javaJob = new Job("J001", "Java TA", "Labs", "Java",
                List.of("Java", "Communication"), 6, 2, JobStatus.OPEN, "U900");

        List<String> missing = aiController.getMissingSkills(bob, javaJob);

        assertEquals(2, missing.size());
        assertTrue(missing.contains("Java"));
        assertTrue(missing.contains("Communication"));
    }

    @Test
    void getWorkloadReportReturnsAllStudents() throws Exception {
        List<WorkloadReport> reports = aiController.getWorkloadReport();

        assertEquals(2, reports.size());
    }

    @Test
    void getStudentWeeklyHoursReturnsCorrectValue() throws Exception {
        assertEquals(6, aiController.getStudentWeeklyHours("S001"));
        assertEquals(0, aiController.getStudentWeeklyHours("S002"));
    }

    @Test
    void isStudentOverloadedReturnsFalseForNormalLoad() throws Exception {
        assertFalse(aiController.isStudentOverloaded("S001"));
    }

    @Test
    void rankWithWorkloadBalanceIncludesCapacityInfo() throws Exception {
        List<MatchResult> ranked = aiController.rankWithWorkloadBalance("J001");

        assertFalse(ranked.isEmpty());
        assertTrue(ranked.get(0).getExplanation().contains("Combined score"));
    }
}
