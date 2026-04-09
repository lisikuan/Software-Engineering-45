package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;

import java.util.List;

public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getAllJobs() throws DataAccessException {
        return jobRepository.findAll();
    }

    public Job getJobById(String jobId) throws ValidationException, BusinessException, DataAccessException {
        if (jobId == null || jobId.isBlank()) {
            throw new ValidationException("Job id must not be blank.");
        }

        return jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException("Job not found for id: " + jobId));
    }
}
