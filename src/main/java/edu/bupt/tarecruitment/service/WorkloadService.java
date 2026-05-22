package edu.bupt.tarecruitment.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.MatchResult;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.WorkloadReport;
import edu.bupt.tarecruitment.persistence.repository.ApplicationRepository;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;

/**
 * Service for monitoring and balancing TA workloads.
 *
 * Responsibilities:
 * 1. Compute per-student workload (total weekly hours from approved applications).
 * 2. Generate workload reports for the Admin overview panel.
 * 3. Provide workload-aware ranking: when multiple applicants fit a job equally
 *    well by skill, prefer the one with the lightest current load.
 */
public class WorkloadService {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final AiMatchingService aiMatchingService;
    private final int maxWeeklyHours;

    public WorkloadService(StudentRepository studentRepository,
                           JobRepository jobRepository,
                           ApplicationRepository applicationRepository,
                           AiMatchingService aiMatchingService) {
        this(studentRepository, jobRepository, applicationRepository,
                aiMatchingService, WorkloadReport.DEFAULT_MAX_WEEKLY_HOURS);
    }

    public WorkloadService(StudentRepository studentRepository,
                           JobRepository jobRepository,
                           ApplicationRepository applicationRepository,
                           AiMatchingService aiMatchingService,
                           int maxWeeklyHours) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.aiMatchingService = aiMatchingService;
        this.maxWeeklyHours = maxWeeklyHours;
    }

    // ----------------------------------------------- single-student workload

    /**
     * Calculate the total weekly hours a student is committed to
     * through approved applications.
     */
    public int getStudentWeeklyHours(String studentId) throws DataAccessException {
        List<Application> approved = applicationRepository.findAll().stream()
                .filter(a -> studentId.equals(a.getStudentId()))
                .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                .toList();

        int totalHours = 0;
        for (Application app : approved) {
            Job job = jobRepository.findById(app.getJobId()).orElse(null);
            if (job != null) {
                totalHours += job.getWeeklyHours();
            }
        }
        return totalHours;
    }

    /**
     * Check whether a student's current workload exceeds the threshold.
     */
    public boolean isOverloaded(String studentId) throws DataAccessException {
        return getStudentWeeklyHours(studentId) >= maxWeeklyHours;
    }

    // ----------------------------------------------- full workload report

    /**
     * Produce a workload report for every registered student.
     * This is the data source for the Admin "Check TA Workload" panel.
     */
    public List<WorkloadReport> generateWorkloadReport() throws DataAccessException {
        List<Student> students = studentRepository.findAll();
        List<Application> allApplications = applicationRepository.findAll();
        List<Job> allJobs = jobRepository.findAll();

        // Pre-index jobs by id for fast lookup
        Map<String, Job> jobMap = allJobs.stream()
                .collect(Collectors.toMap(Job::getId, j -> j, (a, b) -> a));

        List<WorkloadReport> reports = new ArrayList<>();
        for (Student student : students) {
            List<Application> approved = allApplications.stream()
                    .filter(a -> student.getId().equals(a.getStudentId()))
                    .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                    .toList();

            int totalHours = approved.stream()
                    .map(a -> jobMap.get(a.getJobId()))
                    .filter(j -> j != null)
                    .mapToInt(Job::getWeeklyHours)
                    .sum();

            reports.add(new WorkloadReport(
                    student.getId(),
                    student.getName(),
                    approved.size(),
                    totalHours,
                    totalHours >= maxWeeklyHours
            ));
        }
        return reports;
    }

    // ----------------------------------------------- workload-balanced ranking

    /**
     * Rank applicants for a job considering both skill match AND workload balance.
     *
     * Combined score = 0.7 * skillScore + 0.3 * loadScore
     * where loadScore = 1.0 - (currentHours / maxWeeklyHours), clamped to [0, 1].
     *
     * This ensures that, among equally skilled candidates, the one with more
     * available capacity is ranked higher — achieving the "Balancing workload"
     * AI feature required by the spec.
     */
    public List<MatchResult> rankWithWorkloadBalance(String jobId)
            throws ValidationException, DataAccessException {

        List<MatchResult> skillRanking = aiMatchingService.rankApplicantsForJob(jobId);

        List<MatchResult> combined = new ArrayList<>();
        for (MatchResult mr : skillRanking) {
            int currentHours = getStudentWeeklyHours(mr.getStudentId());
            double loadScore = 1.0 - ((double) currentHours / maxWeeklyHours);
            loadScore = Math.max(0.0, Math.min(1.0, loadScore));

            double combinedScore = 0.7 * mr.getScore() + 0.3 * loadScore;
            combinedScore = Math.min(1.0, combinedScore);

            String enhancedExplanation = mr.getExplanation()
                    + String.format("  Current workload: %d hrs/week (capacity score: %.0f%%)\n",
                    currentHours, loadScore * 100)
                    + String.format("  Combined score (70%% skill + 30%% capacity): %.0f%%\n",
                    combinedScore * 100);

            combined.add(new MatchResult(
                    mr.getStudentId(), mr.getJobId(), combinedScore,
                    mr.getMatchedSkills(), mr.getMissingSkills(),
                    enhancedExplanation
            ));
        }
        combined.sort(Comparator.comparingDouble(MatchResult::getScore).reversed());
        return combined;
    }
}
