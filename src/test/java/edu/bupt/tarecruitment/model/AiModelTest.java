package edu.bupt.tarecruitment.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiModelTest {

    // ---------------------------------------- MatchResult

    @Test
    void matchResultStoresAllFields() {
        MatchResult result = new MatchResult(
                "S001", "J001", 0.75,
                List.of("Java"), List.of("SQL"),
                "Some explanation"
        );

        assertEquals("S001", result.getStudentId());
        assertEquals("J001", result.getJobId());
        assertEquals(0.75, result.getScore(), 0.001);
        assertEquals(1, result.getMatchedSkills().size());
        assertEquals("Java", result.getMatchedSkills().get(0));
        assertEquals(1, result.getMissingSkills().size());
        assertEquals("SQL", result.getMissingSkills().get(0));
        assertEquals("Some explanation", result.getExplanation());
    }

    @Test
    void matchResultToStringContainsScore() {
        MatchResult result = new MatchResult("S001", "J001", 0.85, List.of(), List.of(), "");

        String str = result.toString();

        assertNotNull(str);
        assertTrue(str.contains("0.85"));
        assertTrue(str.contains("S001"));
        assertTrue(str.contains("J001"));
    }

    // ---------------------------------------- WorkloadReport

    @Test
    void workloadReportStoresAllFields() {
        WorkloadReport report = new WorkloadReport("S001", "Alice", 3, 18, false);

        assertEquals("S001", report.getStudentId());
        assertEquals("Alice", report.getStudentName());
        assertEquals(3, report.getApprovedJobCount());
        assertEquals(18, report.getTotalWeeklyHours());
        assertFalse(report.isOverloaded());
    }

    @Test
    void workloadReportOverloadedFlag() {
        WorkloadReport report = new WorkloadReport("S001", "Alice", 4, 25, true);

        assertTrue(report.isOverloaded());
    }

    @Test
    void workloadReportToStringContainsName() {
        WorkloadReport report = new WorkloadReport("S001", "Alice", 2, 12, false);

        String str = report.toString();

        assertNotNull(str);
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("S001"));
    }

    @Test
    void defaultMaxWeeklyHoursIs20() {
        assertEquals(20, WorkloadReport.DEFAULT_MAX_WEEKLY_HOURS);
    }
}
