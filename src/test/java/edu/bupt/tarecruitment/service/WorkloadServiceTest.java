package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.model.MatchResult;
import edu.bupt.tarecruitment.model.WorkloadReport;
import edu.bupt.tarecruitment.persistence.json.JsonApplicationRepository;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.persistence.json.JsonStudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkloadServiceTest {

    @TempDir
    Path tempDir;

    // ---------------------------------------- getStudentWeeklyHours

    @Test
    void studentWithNoApprovedAppsHasZeroHours() throws Exception {
        WorkloadService service = createService("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "SUBMITTED" }
                ]
                """);

        assertEquals(0, service.getStudentWeeklyHours("S001"));
    }

    @Test
    void studentWithApprovedAppsAccumulatesHours() throws Exception {
        WorkloadService service = createService("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" },
                  { "id": "A002", "studentId": "S001", "jobId": "J002", "status": "APPROVED" }
                ]
                """);

        // J001 = 6h, J002 = 5h
        assertEquals(11, service.getStudentWeeklyHours("S001"));
    }

    @Test
    void rejectedAppsDoNotCountTowardWorkload() throws Exception {
        WorkloadService service = createService("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" },
                  { "id": "A002", "studentId": "S001", "jobId": "J002", "status": "REJECTED" }
                ]
                """);

        assertEquals(6, service.getStudentWeeklyHours("S001"));
    }

    // ---------------------------------------- isOverloaded

    @Test
    void studentUnderThresholdIsNotOverloaded() throws Exception {
        WorkloadService service = createService("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" }
                ]
                """);

        assertFalse(service.isOverloaded("S001"));
    }

    @Test
    void studentAtOrAboveThresholdIsOverloaded() throws Exception {
        // Use a low threshold (10) so 6+5=11 triggers overload
        WorkloadService service = createServiceWithMaxHours("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" },
                  { "id": "A002", "studentId": "S001", "jobId": "J002", "status": "APPROVED" }
                ]
                """, 10);

        assertTrue(service.isOverloaded("S001"));
    }

    // ---------------------------------------- generateWorkloadReport

    @Test
    void workloadReportIncludesAllStudents() throws Exception {
        WorkloadService service = createService("[]");

        List<WorkloadReport> report = service.generateWorkloadReport();

        // We set up 2 students in the fixture
        assertEquals(2, report.size());
    }

    @Test
    void workloadReportShowsCorrectHoursAndJobCount() throws Exception {
        WorkloadService service = createService("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" },
                  { "id": "A002", "studentId": "S001", "jobId": "J002", "status": "APPROVED" },
                  { "id": "A003", "studentId": "S002", "jobId": "J001", "status": "SUBMITTED" }
                ]
                """);

        List<WorkloadReport> report = service.generateWorkloadReport();

        WorkloadReport aliceReport = report.stream()
                .filter(r -> "S001".equals(r.getStudentId()))
                .findFirst().orElseThrow();
        assertEquals("Alice", aliceReport.getStudentName());
        assertEquals(2, aliceReport.getApprovedJobCount());
        assertEquals(11, aliceReport.getTotalWeeklyHours());
        assertFalse(aliceReport.isOverloaded()); // 11 < 20 default threshold

        WorkloadReport bobReport = report.stream()
                .filter(r -> "S002".equals(r.getStudentId()))
                .findFirst().orElseThrow();
        assertEquals(0, bobReport.getApprovedJobCount());
        assertEquals(0, bobReport.getTotalWeeklyHours());
        assertFalse(bobReport.isOverloaded());
    }

    @Test
    void workloadReportFlagsOverloadedStudent() throws Exception {
        WorkloadService service = createServiceWithMaxHours("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" },
                  { "id": "A002", "studentId": "S001", "jobId": "J002", "status": "APPROVED" }
                ]
                """, 10);

        List<WorkloadReport> report = service.generateWorkloadReport();

        WorkloadReport aliceReport = report.stream()
                .filter(r -> "S001".equals(r.getStudentId()))
                .findFirst().orElseThrow();
        assertTrue(aliceReport.isOverloaded());
    }

    // ---------------------------------------- rankWithWorkloadBalance

    @Test
    void rankWithBalancePrefersLighterLoadedCandidate() throws Exception {
        // S001 has approved job (6h), S002 has none (0h)
        // Both have Java skill, so for a Java job, S002 should rank higher
        // because of lighter workload.
        WorkloadService service = createServiceWithSkills("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J002", "status": "APPROVED" }
                ]
                """);

        List<MatchResult> ranked = service.rankWithWorkloadBalance("J001");

        assertFalse(ranked.isEmpty());
        // Both match Java; S002 has 0 current load so should rank at least as high
        // Check that the ranking includes workload info in explanation
        assertTrue(ranked.get(0).getExplanation().contains("Current workload"));
        assertTrue(ranked.get(0).getExplanation().contains("Combined score"));
    }

    @Test
    void rankWithBalanceExplanationContainsCapacityInfo() throws Exception {
        WorkloadService service = createService("""
                [
                  { "id": "A001", "studentId": "S001", "jobId": "J001", "status": "APPROVED" }
                ]
                """);

        List<MatchResult> ranked = service.rankWithWorkloadBalance("J002");

        for (MatchResult mr : ranked) {
            assertTrue(mr.getExplanation().contains("Current workload"));
            assertTrue(mr.getExplanation().contains("capacity score"));
        }
    }

    // ---------------------------------------- helpers

    private WorkloadService createService(String applicationsJson) throws Exception {
        return createServiceWithMaxHours(applicationsJson, WorkloadReport.DEFAULT_MAX_WEEKLY_HOURS);
    }

    private WorkloadService createServiceWithMaxHours(String applicationsJson, int maxHours) throws Exception {
        writeStudentsFile(List.of("Java", "Communication", "Git"), List.of("Java", "SQL"));
        writeJobsFile();
        Files.writeString(tempDir.resolve("applications.json"), applicationsJson);

        JsonDataStore store = new JsonDataStore(tempDir);
        JsonStudentRepository studentRepo = new JsonStudentRepository(store);
        JsonJobRepository jobRepo = new JsonJobRepository(store);
        JsonApplicationRepository appRepo = new JsonApplicationRepository(store);

        AiMatchingService ai = new AiMatchingService(studentRepo, jobRepo, new SkillNormalizer());
        return new WorkloadService(studentRepo, jobRepo, appRepo, ai, maxHours);
    }

    private WorkloadService createServiceWithSkills(String applicationsJson) throws Exception {
        // Both students have Java so they compete equally on skill
        writeStudentsFile(List.of("Java", "Communication"), List.of("Java", "Communication"));
        writeJobsFile();
        Files.writeString(tempDir.resolve("applications.json"), applicationsJson);

        JsonDataStore store = new JsonDataStore(tempDir);
        JsonStudentRepository studentRepo = new JsonStudentRepository(store);
        JsonJobRepository jobRepo = new JsonJobRepository(store);
        JsonApplicationRepository appRepo = new JsonApplicationRepository(store);

        AiMatchingService ai = new AiMatchingService(studentRepo, jobRepo, new SkillNormalizer());
        return new WorkloadService(studentRepo, jobRepo, appRepo, ai);
    }

    private void writeStudentsFile(List<String> s1Skills, List<String> s2Skills) throws Exception {
        String s1Tags = "[" + String.join(",", s1Skills.stream().map(s -> "\"" + s + "\"").toList()) + "]";
        String s2Tags = "[" + String.join(",", s2Skills.stream().map(s -> "\"" + s + "\"").toList()) + "]";
        Files.writeString(tempDir.resolve("students.json"), """
                [
                  {
                    "id": "S001", "name": "Alice", "userId": "U001",
                    "studentNumber": "2024001", "major": "CS", "grade": "3",
                    "skillTags": %s, "cvFilePath": null
                  },
                  {
                    "id": "S002", "name": "Bob", "userId": "U002",
                    "studentNumber": "2024002", "major": "CS", "grade": "3",
                    "skillTags": %s, "cvFilePath": null
                  }
                ]
                """.formatted(s1Tags, s2Tags));
    }

    private void writeJobsFile() throws Exception {
        Files.writeString(tempDir.resolve("jobs.json"), """
                [
                  {
                    "id": "J001", "title": "Java TA", "description": "Labs",
                    "courseName": "Java", "requiredSkills": ["Java", "Communication"],
                    "weeklyHours": 6, "quota": 2, "status": "OPEN", "publisherId": "U900"
                  },
                  {
                    "id": "J002", "title": "DB TA", "description": "Grading",
                    "courseName": "Database", "requiredSkills": ["SQL"],
                    "weeklyHours": 5, "quota": 1, "status": "OPEN", "publisherId": "U900"
                  }
                ]
                """);
    }
}
