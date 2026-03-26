package edu.bupt.tarecruitment.controller;

import edu.bupt.tarecruitment.service.ApplicationService;

public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public ApplicationService getApplicationService() {
        return applicationService;
    }
}
