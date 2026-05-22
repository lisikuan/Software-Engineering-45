package edu.bupt.tarecruitment.controller;

import java.util.List;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.service.JobService;

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

    public Job publishJob(String courseName, List<String> requiredSkills, int weeklyHours, int quota, String description, String publisherId)
            throws ValidationException, DataAccessException {
        return jobService.publishJob(courseName, requiredSkills, weeklyHours, quota, description, publisherId);
    }

    public List<Job> getOpenJobs() throws DataAccessException {
        return jobService.getOpenJobs();
    }

    public List<Job> getJobsByPublisher(String publisherId) throws DataAccessException {
        return jobService.getJobsByPublisher(publisherId);
    }

    public Job updateOwnJob(
            String jobId,
            String courseName,
            List<String> requiredSkills,
            int weeklyHours,
            int quota,
            String description,
            String publisherId
    ) throws ValidationException, BusinessException, DataAccessException {
        return jobService.updateOwnJob(jobId, courseName, requiredSkills, weeklyHours, quota, description, publisherId);
    }

    public boolean deleteOwnJob(String jobId, String publisherId)
            throws ValidationException, BusinessException, DataAccessException {
        return jobService.deleteOwnJob(jobId, publisherId);
    }
}
