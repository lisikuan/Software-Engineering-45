package edu.bupt.tarecruitment.model;

import java.util.List;

/**
 * Holds the AI-computed match result between a Student and a Job.
 * Provides a numeric score (0.0–1.0) plus an explainable breakdown
 * so the system "combines structured logic with AI-based interpretation"
 * as required by the coursework spec.
 */
public class MatchResult {
    private final String studentId;
    private final String jobId;
    private final double score;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String explanation;

    public MatchResult(String studentId, String jobId, double score,
                       List<String> matchedSkills, List<String> missingSkills,
                       String explanation) {
        this.studentId = studentId;
        this.jobId = jobId;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.explanation = explanation;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getJobId() {
        return jobId;
    }

    public double getScore() {
        return score;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return String.format("MatchResult{student=%s, job=%s, score=%.2f, matched=%s, missing=%s}",
                studentId, jobId, score, matchedSkills, missingSkills);
    }
}
