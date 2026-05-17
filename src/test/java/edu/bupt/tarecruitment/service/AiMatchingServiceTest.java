package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.JobStatus;
import edu.bupt.tarecruitment.model.MatchResult;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.persistence.json.JsonStudentRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiMatchingServiceTest {

    @TempDir
    Path tempDir;

    private AiMatchingService aiMatchingService;
    private JsonStudentRepository studentRepository;
    private JsonJobRepository jobRepository;

    @BeforeEach
    void setUp() throws Exception {
        Files.writeString(tempDir.resolve("students.json"), """
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001",
                    "studentNumber": "2024001",
                    "skillTags": ["Java", "Communication", "Git"],
                    "cvFilePath": "cvs/S001.pdf"
                  },
                  {
                    "id": "S002",
                    "name": "Bob",
                    "userId": "U002",
                    "studentNumber": "2024002",
                    "skillTags": ["Python", "SQL", "Machine Learning"],
                    "cvFilePath": "cvs/S002.pdf"
                  },
                  {
                    "id": "S003",
                    "name": "Charlie",
                    "userId": "U003",
                    "studentNumber": "2024003",
                    "skillTags": [],
                    "cvFilePath": null
                  }
                ]
                """);
        Files.writeString(tempDir.resolve("jobs.json"), """
                [
                  {
                    "id": "J001",
                    "title": "Java Programming TA",
                    "description": "Assist with Java labs",
                    "courseName": "Java Programming",
                    "requiredSkills": ["Java", "Communication"],
                    "weeklyHours": 6,
                    "quota": 2,
                    "status": "OPEN",
                    "publisherId": "U900"
                  },
                  {
                    "id": "J002",
                    "title": "Database Systems TA",
                    "description": "Support database coursework",
                    "courseName": "Database Systems",
                    "requiredSkills": ["SQL", "Tutoring"],
                    "weeklyHours": 5,
                    "quota": 1,
                    "status": "OPEN",
                    "publisherId": "U900"
                  },
                  {
                    "id": "J003",
                    "title": "ML Research TA",
                    "description": "Help with ML research",
                    "courseName": "Machine Learning",
                    "requiredSkills": ["Python", "Machine Learning", "Data Analysis"],
                    "weeklyHours": 8,
                    "quota": 1,
                    "status": "CLOSED",
                    "publisherId": "U900"
                  }
                ]
                """);
        // applications.json needed by data store but not used directly
        Files.writeString(tempDir.resolve("applications.json"), "[]");

        JsonDataStore store = new JsonDataStore(tempDir);
        studentRepository = new JsonStudentRepository(store);
        jobRepository = new JsonJobRepository(store);
        aiMatchingService = new AiMatchingService(studentRepository, jobRepository, new SkillNormalizer());
    }

    // ------------------------------------------------- match single pair

    @Test
    void matchReturnsFullScoreWhenAllSkillsMatch() {
        Student alice = buildStudent("S001", "Alice", List.of("Java", "Communication"));
        Job javaJob = buildJob("J001", "Java TA", List.of("Java", "Communication"));

        MatchResult result = aiMatchingService.match(alice, javaJob);

        assertEquals(1.0, result.getScore(), 0.001);
        assertEquals(2, result.getMatchedSkills().size());
        assertTrue(result.getMissingSkills().isEmpty());
    }

    @Test
    void matchReturnsPartialScoreForPartialMatch() {
        Student alice = buildStudent("S001", "Alice", List.of("Java"));
        Job javaJob = buildJob("J001", "Java TA", List.of("Java", "Communication"));

        MatchResult result = aiMatchingService.match(alice, javaJob);

        assertEquals(0.5, result.getScore(), 0.001);
        assertEquals(1, result.getMatchedSkills().size());
        assertEquals(1, result.getMissingSkills().size());
        assertTrue(result.getMissingSkills().contains("Communication"));
    }

    @Test
    void matchReturnsZeroScoreWhenNoSkillsMatch() {
        Student charlie = buildStudent("S003", "Charlie", List.of());
        Job javaJob = buildJob("J001", "Java TA", List.of("Java", "Communication"));

        MatchResult result = aiMatchingService.match(charlie, javaJob);

        assertEquals(0.0, result.getScore(), 0.001);
        assertEquals(2, result.getMissingSkills().size());
    }

    @Test
    void matchGivesBonusForExtraSkills() {
        Student alice = buildStudent("S001", "Alice", List.of("Java", "Communication", "Git", "Linux"));
        Job javaJob = buildJob("J001", "Java TA", List.of("Java", "Communication"));

        MatchResult result = aiMatchingService.match(alice, javaJob);

        // Base 1.0 + 0.1 bonus, capped at 1.0
        assertEquals(1.0, result.getScore(), 0.001);
    }

    @Test
    void matchHandlesJobWithNoRequiredSkills() {
        Student alice = buildStudent("S001", "Alice", List.of("Java"));
        Job emptyJob = buildJob("J999", "General TA", List.of());

        MatchResult result = aiMatchingService.match(alice, emptyJob);

        // No requirements means anyone matches, plus bonus for extra skills
        assertEquals(1.0, result.getScore(), 0.001);
    }

    // ------------------------------------------------- synonym-aware matching

    @Test
    void matchRecognisesSynonyms() {
        // Student has "MySQL" which should match job requiring "SQL"
        Student bob = buildStudent("S002", "Bob", List.of("MySQL"));
        Job dbJob = buildJob("J002", "DB TA", List.of("SQL"));

        MatchResult result = aiMatchingService.match(bob, dbJob);

        assertEquals(1.0, result.getScore(), 0.001);
        assertEquals(1, result.getMatchedSkills().size());
    }

    @Test
    void matchRecognisesTeachingAsTutoring() {
        Student s = buildStudent("S010", "Dave", List.of("Teaching", "SQL"));
        Job j = buildJob("J010", "DB TA", List.of("SQL", "Tutoring"));

        MatchResult result = aiMatchingService.match(s, j);

        assertEquals(1.0, result.getScore(), 0.001);
    }

    // ------------------------------------------------- explanation

    @Test
    void matchExplanationContainsVerdictAndSkills() {
        Student alice = buildStudent("S001", "Alice", List.of("Java"));
        Job javaJob = buildJob("J001", "Java TA", List.of("Java", "Communication"));

        MatchResult result = aiMatchingService.match(alice, javaJob);

        assertNotNull(result.getExplanation());
        assertTrue(result.getExplanation().contains("MODERATE match"));
        assertTrue(result.getExplanation().contains("Missing skills"));
        assertTrue(result.getExplanation().contains("Communication"));
    }

    @Test
    void strongMatchExplanationSaysStrongMatch() {
        Student s = buildStudent("S001", "Alice", List.of("Java", "Communication"));
        Job j = buildJob("J001", "Java TA", List.of("Java", "Communication"));

        MatchResult result = aiMatchingService.match(s, j);
        assertTrue(result.getExplanation().contains("STRONG match"));
    }

    @Test
    void weakMatchExplanationSaysWeakMatch() {
        Student s = buildStudent("S001", "Alice", List.of("Cooking"));
        Job j = buildJob("J001", "Java TA", List.of("Java", "Communication", "SQL"));

        MatchResult result = aiMatchingService.match(s, j);
        assertTrue(result.getExplanation().contains("WEAK match"));
    }

    // ------------------------------------------------- rankApplicantsForJob

    @Test
    void rankApplicantsReturnsDescendingOrder() throws Exception {
        List<MatchResult> ranked = aiMatchingService.rankApplicantsForJob("J001");

        assertFalse(ranked.isEmpty());
        // Alice (Java, Communication, Git) should rank higher than Bob (Python, SQL, ML)
        assertEquals("S001", ranked.get(0).getStudentId());
        for (int i = 0; i < ranked.size() - 1; i++) {
            assertTrue(ranked.get(i).getScore() >= ranked.get(i + 1).getScore());
        }
    }

    @Test
    void rankApplicantsThrowsForBlankJobId() {
        assertThrows(Exception.class, () -> aiMatchingService.rankApplicantsForJob(""));
    }

    @Test
    void rankApplicantsThrowsForNonExistentJob() {
        assertThrows(Exception.class, () -> aiMatchingService.rankApplicantsForJob("J999"));
    }

    // ------------------------------------------------- rankJobsForStudent

    @Test
    void rankJobsReturnsOnlyOpenJobs() throws Exception {
        List<MatchResult> ranked = aiMatchingService.rankJobsForStudent("S002");

        // J003 is CLOSED, so only J001 and J002 should appear
        assertEquals(2, ranked.size());
        assertTrue(ranked.stream().noneMatch(r -> "J003".equals(r.getJobId())));
    }

    @Test
    void rankJobsOrdersByScoreDescending() throws Exception {
        List<MatchResult> ranked = aiMatchingService.rankJobsForStudent("S001");

        for (int i = 0; i < ranked.size() - 1; i++) {
            assertTrue(ranked.get(i).getScore() >= ranked.get(i + 1).getScore());
        }
    }

    @Test
    void rankJobsThrowsForBlankStudentId() {
        assertThrows(Exception.class, () -> aiMatchingService.rankJobsForStudent(""));
    }

    // ------------------------------------------------- getMissingSkills

    @Test
    void getMissingSkillsReturnsCorrectGaps() {
        Student alice = buildStudent("S001", "Alice", List.of("Java"));
        Job javaJob = buildJob("J001", "Java TA", List.of("Java", "Communication", "SQL"));

        List<String> missing = aiMatchingService.getMissingSkills(alice, javaJob);

        assertEquals(2, missing.size());
        assertTrue(missing.contains("Communication"));
        assertTrue(missing.contains("SQL"));
    }

    @Test
    void getMissingSkillsReturnsEmptyWhenAllMatched() {
        Student s = buildStudent("S001", "Alice", List.of("Java", "Communication"));
        Job j = buildJob("J001", "Java TA", List.of("Java", "Communication"));

        List<String> missing = aiMatchingService.getMissingSkills(s, j);
        assertTrue(missing.isEmpty());
    }

    @Test
    void getMissingSkillsUsesNormalisedComparison() {
        // Student has "Teaching" which normalises same as "Tutoring"
        Student s = buildStudent("S001", "Alice", List.of("Teaching"));
        Job j = buildJob("J001", "TA", List.of("Tutoring"));

        List<String> missing = aiMatchingService.getMissingSkills(s, j);
        assertTrue(missing.isEmpty());
    }

    // ------------------------------------------------- helpers

    private Student buildStudent(String id, String name, List<String> skills) {
        return new Student(id, name, "U" + id.substring(1), "20240" + id.substring(1),
                "CS", "3", new ArrayList<>(skills), null);
    }

    private Job buildJob(String id, String title, List<String> skills) {
        return new Job(id, title, "desc", "Course", new ArrayList<>(skills), 6, 2, JobStatus.OPEN, "U900");
    }
}
