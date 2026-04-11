package edu.bupt.tarecruitment.service;
<<<<<<< HEAD

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
=======
>>>>>>> 3671f8efbaf0da2baeaf2e256d12a477dd7b649b

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.JobStatus;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;
import edu.bupt.tarecruitment.validation.JobValidator;

public class JobService {
    private final JobRepository jobRepository;
    private final JobValidator jobValidator;

    public JobService(JobRepository jobRepository, JobValidator jobValidator) {
        this.jobRepository = jobRepository;
        this.jobValidator = jobValidator;
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

    public Job publishJob(
            String courseName,
            List<String> requiredSkills,
            int weeklyHours,
            int quota,
            String description,
            String publisherId
    ) throws ValidationException, DataAccessException {
        jobValidator.validateJobInput(courseName, requiredSkills, weeklyHours, quota);

        String id = nextJobId();
        String title = courseName + " TA";
        Job job = new Job(id, title, description == null ? "" : description, courseName, requiredSkills, weeklyHours, quota, JobStatus.OPEN, publisherId);
        return jobRepository.insert(job);
    }

    public List<Job> getOpenJobs() throws DataAccessException {
        return jobRepository.findAll().stream()
                .filter(job -> job.getStatus() == JobStatus.OPEN)
                .toList();
    }

    public List<Job> getJobsByPublisher(String publisherId) throws DataAccessException {
        return jobRepository.findByPublisherId(publisherId);
    }

    private String nextJobId() throws DataAccessException {
        int nextNumber = jobRepository.findAll().stream()
                .map(Job::getId)
                .map(this::extractNumericSuffix)
                .flatMap(Optional::stream)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
        return String.format("J%03d", nextNumber);
    }

    private Optional<Integer> extractNumericSuffix(String jobId) {
        if (jobId == null || jobId.length() < 2 || jobId.charAt(0) != 'J') {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(jobId.substring(1)));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
