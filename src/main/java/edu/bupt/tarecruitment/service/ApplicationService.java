package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import edu.bupt.tarecruitment.persistence.repository.ApplicationRepository;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;
import edu.bupt.tarecruitment.validation.ApplicationValidator;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public Application submitApplication(String studentId, String jobId)
            throws ValidationException, BusinessException, DataAccessException {
        applicationValidator.validateSubmission(studentId, jobId);
        ensureStudentAndJobExist(studentId, jobId);
        ensureApplicationIsUnique(studentId, jobId);

        Application application = new Application(nextApplicationId(), studentId, jobId, ApplicationStatus.SUBMITTED);
        return applicationRepository.insert(application);
    }

    public Application updateApplicationStatus(String applicationId, ApplicationStatus newStatus)
            throws ValidationException, BusinessException, DataAccessException {
        applicationValidator.validateStatusUpdate(applicationId, newStatus);

        Application existingApplication = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found for id: " + applicationId));

        if (existingApplication.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new BusinessException("Only submitted applications can be reviewed.");
        }

        if (newStatus == ApplicationStatus.SUBMITTED) {
            throw new BusinessException("Reviewed application status must be APPROVED or REJECTED.");
        }

        existingApplication.setStatus(newStatus);
        return applicationRepository.update(existingApplication);
    }

    private void ensureStudentAndJobExist(String studentId, String jobId) throws BusinessException, DataAccessException {
        if (studentRepository.findById(studentId).isEmpty()) {
            throw new BusinessException("Student not found for id: " + studentId);
        }
        if (jobRepository.findById(jobId).isEmpty()) {
            throw new BusinessException("Job not found for id: " + jobId);
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
