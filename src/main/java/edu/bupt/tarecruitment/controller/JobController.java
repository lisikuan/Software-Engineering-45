package edu.bupt.tarecruitment.controller;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.service.JobService;

import java.util.List;

public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    public List<Job> getAllJobs() throws DataAccessException {
        return jobService.getAllJobs();
    }

    public Job getJobById(String jobId) throws ValidationException, BusinessException, DataAccessException {
        return jobService.getJobById(jobId);
    }

    public Job publishJob(String courseName, List<String> requiredSkills, int weeklyHours, String description)
            throws ValidationException, DataAccessException {
        return jobService.publishJob(courseName, requiredSkills, weeklyHours, description);
    }
}
