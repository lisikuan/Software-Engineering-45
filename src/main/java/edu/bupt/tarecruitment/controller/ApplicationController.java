package edu.bupt.tarecruitment.controller;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import edu.bupt.tarecruitment.service.ApplicationService;

import java.util.List;

public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public List<Application> getAllApplications() throws DataAccessException {
        return applicationService.getAllApplications();
    }

    public List<Application> getApplicationsForStudent(String studentId) throws ValidationException, DataAccessException {
        return applicationService.getApplicationsForStudent(studentId);
    }

    public Application submitApplication(String studentId, String jobId)
            throws ValidationException, BusinessException, DataAccessException {
        return applicationService.submitApplication(studentId, jobId);
    }

    public Application updateApplicationStatus(String applicationId, ApplicationStatus newStatus)
            throws ValidationException, BusinessException, DataAccessException {
        return applicationService.updateApplicationStatus(applicationId, newStatus);
    }
}
