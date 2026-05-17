package edu.bupt.tarecruitment.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.MatchResult;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;

/**
 * AI-powered matching service that ranks applicants against jobs and vice versa.
 *
 * Design rationale (as required by coursework):
 * - Uses structured logic (synonym normalisation, Jaccard-like scoring) combined
 *   with interpretable heuristics rather than an opaque ML model.
 * - Every result includes an explanation so outcomes are NOT "blindly accepted".
 * - The scoring formula is:
 *     score = matchedCount / max(requiredCount, 1)
 *   capped at 1.0, with a bonus of +0.1 if the student has extra relevant skills.
 */
public class AiMatchingService {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final SkillNormalizer skillNormalizer;

    public AiMatchingService(StudentRepository studentRepository,
                             JobRepository jobRepository,
                             SkillNormalizer skillNormalizer) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.skillNormalizer = skillNormalizer;
    }

    // ------------------------------------------------------------------ match

    /**
     * Compute the match result between a single student and a single job.
     */
    public MatchResult match(Student student, Job job) {
        List<String> requiredNorm = normalizeList(job.getRequiredSkills());
        List<String> studentNorm = normalizeList(student.getSkillTags());

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (int i = 0; i < requiredNorm.size(); i++) {
            String reqNorm = requiredNorm.get(i);
            if (studentNorm.contains(reqNorm)) {
                matched.add(job.getRequiredSkills().get(i));
            } else {
                missing.add(job.getRequiredSkills().get(i));
            }
        }

        double baseScore = requiredNorm.isEmpty()
                ? 1.0
                : (double) matched.size() / requiredNorm.size();

        // Bonus: student has extra skills beyond what the job requires
        long extraSkillCount = studentNorm.stream()
                .filter(s -> !requiredNorm.contains(s))
                .count();
        double bonus = extraSkillCount > 0 ? 0.1 : 0.0;
        double finalScore = Math.min(1.0, baseScore + bonus);

        String explanation = buildExplanation(student, job, matched, missing, finalScore);
        return new MatchResult(student.getId(), job.getId(), finalScore, matched, missing, explanation);
    }

    // ------------------------------------------------- rank applicants for job

    /**
     * Rank all registered students against a specific job.
     * Returns results sorted by score descending.
     */
    public List<MatchResult> rankApplicantsForJob(String jobId)
            throws ValidationException, DataAccessException {
        if (jobId == null || jobId.isBlank()) {
            throw new ValidationException("Job id must not be blank.");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ValidationException("Job not found: " + jobId));

        return studentRepository.findAll().stream()
                .map(student -> match(student, job))
                .sorted(Comparator.comparingDouble(MatchResult::getScore).reversed())
                .collect(Collectors.toList());
    }

    // ------------------------------------------------- rank jobs for student

    /**
     * Rank all open jobs for a specific student.
     * Returns results sorted by score descending.
     */
    public List<MatchResult> rankJobsForStudent(String studentId)
            throws ValidationException, DataAccessException {
        if (studentId == null || studentId.isBlank()) {
            throw new ValidationException("Student id must not be blank.");
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ValidationException("Student not found: " + studentId));

        return jobRepository.findAll().stream()
                .filter(job -> job.getStatus() == edu.bupt.tarecruitment.model.JobStatus.OPEN)
                .map(job -> match(student, job))
                .sorted(Comparator.comparingDouble(MatchResult::getScore).reversed())
                .collect(Collectors.toList());
    }

    // ------------------------------------------------- missing skills

    /**
     * Identify which skills a student is missing for a specific job,
     * using normalised comparison.
     */
    public List<String> getMissingSkills(Student student, Job job) {
        List<String> studentNorm = normalizeList(student.getSkillTags());
        List<String> missing = new ArrayList<>();
        List<String> requiredNorm = normalizeList(job.getRequiredSkills());

        for (int i = 0; i < requiredNorm.size(); i++) {
            if (!studentNorm.contains(requiredNorm.get(i))) {
                missing.add(job.getRequiredSkills().get(i));
            }
        }
        return missing;
    }

    // ------------------------------------------------- helpers

    private List<String> normalizeList(List<String> skills) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream()
                .map(skillNormalizer::normalize)
                .collect(Collectors.toList());
    }

    private String buildExplanation(Student student, Job job,
                                    List<String> matched, List<String> missing,
                                    double score) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Match analysis for student '%s' against job '%s':\n",
                student.getName(), job.getTitle()));
        sb.append(String.format("  Overall score: %.0f%%\n", score * 100));

        if (!matched.isEmpty()) {
            sb.append("  Matched skills: ").append(String.join(", ", matched)).append("\n");
        }
        if (!missing.isEmpty()) {
            sb.append("  Missing skills: ").append(String.join(", ", missing)).append("\n");
            sb.append("  Recommendation: The applicant should develop skills in ")
                    .append(String.join(", ", missing))
                    .append(" to improve their match.\n");
        }
        if (score >= 0.8) {
            sb.append("  Verdict: STRONG match – highly recommended.\n");
        } else if (score >= 0.5) {
            sb.append("  Verdict: MODERATE match – acceptable with some skill gaps.\n");
        } else {
            sb.append("  Verdict: WEAK match – significant skill gaps exist.\n");
        }
        return sb.toString();
    }
}
