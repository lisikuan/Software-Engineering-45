package edu.bupt.tarecruitment.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.JobStatus;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.persistence.repository.ApplicationRepository;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;
import edu.bupt.tarecruitment.validation.ApplicationValidator;

public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final ApplicationValidator applicationValidator;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            JobRepository jobRepository,
            ApplicationValidator applicationValidator
    ) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.applicationValidator = applicationValidator;
    }

    public List<Application> getAllApplications() throws DataAccessException {
        return applicationRepository.findAll();
    }

    public List<Application> getApplicationsForStudent(String studentId) throws ValidationException, DataAccessException {
        if (studentId == null || studentId.isBlank()) {
            throw new ValidationException("Student id must not be blank.");
        }

        return applicationRepository.findAll().stream()
                .filter(application -> studentId.equals(application.getStudentId()))
                .toList();
    }

    public List<Application> getApplicationsBySkills(List<String> skills, String publisherId) throws DataAccessException {
        List<Application> applications = applicationRepository.findAll().stream()
                .filter(application -> {
                    try {
                        Job job = jobRepository.findById(application.getJobId()).orElse(null);
                        if (job == null) {
                            return false;
                        }
                        if (publisherId != null && !publisherId.equals(job.getPublisherId())) {
                            return false;
                        }
                        if (skills == null || skills.isEmpty()) {
                            return true;
                        }
                        Student student = studentRepository.findById(application.getStudentId()).orElse(null);
                        if (student == null || student.getSkillTags() == null) {
                            return false;
                        }
                        return student.getSkillTags().stream()
                                .anyMatch(skill -> skills.stream()
                                        .anyMatch(filter -> filter.equalsIgnoreCase(skill)));
                    } catch (DataAccessException e) {
                        return false;
                    }
                })
                .toList();
        return applications;
    }

    public Application submitApplication(String studentId, String jobId)
            throws ValidationException, BusinessException, DataAccessException {
        applicationValidator.validateSubmission(studentId, jobId);
        Job job = ensureStudentAndJobExist(studentId, jobId);
        ensureJobIsOpen(job);
        ensureStudentProfileReady(studentId);
        ensureApplicationIsUnique(studentId, jobId);

        Application application = new Application(nextApplicationId(), studentId, jobId, ApplicationStatus.SUBMITTED);
        return applicationRepository.insert(application);
    }

    public Application updateApplicationStatus(String applicationId, ApplicationStatus newStatus)
            throws ValidationException, BusinessException, DataAccessException {
        applicationValidator.validateStatusUpdate(applicationId, newStatus);

        if (newStatus == ApplicationStatus.SUBMITTED) {
            throw new BusinessException("Application status cannot be changed back to SUBMITTED.");
        }

        Application existingApplication = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found for id: " + applicationId));

        if (existingApplication.getStatus() == ApplicationStatus.APPROVED
                || existingApplication.getStatus() == ApplicationStatus.REJECTED) {
            throw new BusinessException("Cannot change the status of an already decided application.");
        }

        if (newStatus == ApplicationStatus.REVIEWED) {
            if (existingApplication.getStatus() != ApplicationStatus.SUBMITTED) {
                throw new BusinessException("Only submitted applications can be marked as reviewed.");
            }
        }

        if (newStatus == ApplicationStatus.APPROVED || newStatus == ApplicationStatus.REJECTED) {
            if (existingApplication.getStatus() == ApplicationStatus.APPROVED
                    || existingApplication.getStatus() == ApplicationStatus.REJECTED) {
                throw new BusinessException("Application has already been decided.");
            }
        }

        existingApplication.setStatus(newStatus);
        Application updated = applicationRepository.update(existingApplication);
        if (newStatus == ApplicationStatus.APPROVED) {
            closeJobWhenQuotaFilled(existingApplication.getJobId());
        }
        return updated;
    }

    private Job ensureStudentAndJobExist(String studentId, String jobId) throws BusinessException, DataAccessException {
        if (studentRepository.findById(studentId).isEmpty()) {
            throw new BusinessException("Student not found for id: " + studentId);
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException("Job not found for id: " + jobId));
        return job;
    }

    private void ensureJobIsOpen(Job job) throws BusinessException {
        if (job.getStatus() != JobStatus.OPEN) {
            throw new BusinessException("Cannot apply to a job that is not open.");
        }
    }

    private void ensureStudentProfileReady(String studentId) throws BusinessException, DataAccessException {
        if (studentRepository.findById(studentId).stream().noneMatch(student ->
                student.getStudentNumber() != null && !student.getStudentNumber().isBlank())) {
            throw new BusinessException("Student profile must be completed before applying for a job.");
        }
    }

    private void ensureApplicationIsUnique(String studentId, String jobId) throws BusinessException, DataAccessException {
        boolean duplicateExists = applicationRepository.findAll().stream()
                .anyMatch(application -> studentId.equals(application.getStudentId())
                        && jobId.equals(application.getJobId()));
        if (duplicateExists) {
            throw new BusinessException("The student has already applied for this job.");
        }
    }

    private void closeJobWhenQuotaFilled(String jobId) throws DataAccessException, BusinessException {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException("Job not found while checking quota: " + jobId));
        if (job.getStatus() != JobStatus.OPEN || job.getQuota() <= 0) {
            return;
        }

        long approvedApplicationsCount = applicationRepository.findAll().stream()
                .filter(application -> jobId.equals(application.getJobId())
                        && application.getStatus() == ApplicationStatus.APPROVED)
                .count();
        if (approvedApplicationsCount >= job.getQuota()) {
            job.setStatus(JobStatus.CLOSED);
            jobRepository.update(job);
        }
    }

    private String nextApplicationId() throws DataAccessException {
        List<Application> applications = applicationRepository.findAll();
        int nextNumber = applications.stream()
                .map(Application::getId)
                .map(this::extractNumericSuffix)
                .flatMap(Optional::stream)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
        return String.format("A%03d", nextNumber);
    }

    private Optional<Integer> extractNumericSuffix(String applicationId) {
        if (applicationId == null || applicationId.length() < 2 || applicationId.charAt(0) != 'A') {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(applicationId.substring(1)));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
