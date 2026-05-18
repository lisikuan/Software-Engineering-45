package edu.bupt.tarecruitment.controller;

import java.util.List;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.MatchResult;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.WorkloadReport;
import edu.bupt.tarecruitment.service.AiMatchingService;
import edu.bupt.tarecruitment.service.WorkloadService;

/**
 * Controller bridging the AI services to the UI layer.
 * Provides skill matching, job recommendations, workload reports,
 * and workload-balanced ranking.
 */
public class AiController {
    private final AiMatchingService aiMatchingService;
    private final WorkloadService workloadService;

    public AiController(AiMatchingService aiMatchingService, WorkloadService workloadService) {
        this.aiMatchingService = aiMatchingService;
        this.workloadService = workloadService;
    }

    /** Match a single student against a single job (used in detail views). */
    public MatchResult match(Student student, Job job) {
        return aiMatchingService.match(student, job);
    }

    /** Rank all students by suitability for a given job (MO view). */
    public List<MatchResult> rankApplicantsForJob(String jobId)
            throws ValidationException, DataAccessException {
        return aiMatchingService.rankApplicantsForJob(jobId);
    }

    /** Rank all open jobs by suitability for a given student (TA view). */
    public List<MatchResult> rankJobsForStudent(String studentId)
            throws ValidationException, DataAccessException {
        return aiMatchingService.rankJobsForStudent(studentId);
    }

    /** Get the missing skills for a student relative to a job. */
    public List<String> getMissingSkills(Student student, Job job) {
        return aiMatchingService.getMissingSkills(student, job);
    }

    /** Get full workload report for all students (Admin view). */
    public List<WorkloadReport> getWorkloadReport() throws DataAccessException {
        return workloadService.generateWorkloadReport();
    }

    /** Get a single student's weekly hours. */
    public int getStudentWeeklyHours(String studentId) throws DataAccessException {
        return workloadService.getStudentWeeklyHours(studentId);
    }

    /** Check if a student is overloaded. */
    public boolean isStudentOverloaded(String studentId) throws DataAccessException {
        return workloadService.isOverloaded(studentId);
    }

    /** Rank applicants considering both skill match and workload balance. */
    public List<MatchResult> rankWithWorkloadBalance(String jobId)
            throws ValidationException, DataAccessException {
        return workloadService.rankWithWorkloadBalance(jobId);
    }
}
