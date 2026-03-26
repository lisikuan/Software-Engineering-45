package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.persistence.repository.ApplicationRepository;

public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public ApplicationRepository getApplicationRepository() {
        return applicationRepository;
    }
}
