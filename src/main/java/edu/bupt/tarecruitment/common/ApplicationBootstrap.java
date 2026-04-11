package edu.bupt.tarecruitment.common;

import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.AuthController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.controller.StudentController;
import edu.bupt.tarecruitment.persistence.json.FileCvRepository;
import edu.bupt.tarecruitment.persistence.json.JsonApplicationRepository;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonJobRepository;
import edu.bupt.tarecruitment.persistence.json.JsonStudentRepository;
import edu.bupt.tarecruitment.persistence.json.JsonUserRepository;
import edu.bupt.tarecruitment.presentation.MainFrame;
import edu.bupt.tarecruitment.service.ApplicationService;
import edu.bupt.tarecruitment.service.AuthService;
import edu.bupt.tarecruitment.service.JobService;
import edu.bupt.tarecruitment.service.StudentService;
import edu.bupt.tarecruitment.validation.ApplicationValidator;
import edu.bupt.tarecruitment.validation.AuthValidator;
import edu.bupt.tarecruitment.validation.JobValidator;
import edu.bupt.tarecruitment.validation.StudentValidator;

public class ApplicationBootstrap {
    public MainFrame createMainFrame() {
        JsonDataStore jsonDataStore = new JsonDataStore(PathsConfig.DATA_DIRECTORY);

        JsonStudentRepository studentRepository = new JsonStudentRepository(jsonDataStore);
        JsonJobRepository jobRepository = new JsonJobRepository(jsonDataStore);
        JsonApplicationRepository applicationRepository = new JsonApplicationRepository(jsonDataStore);
        JsonUserRepository userRepository = new JsonUserRepository(jsonDataStore);
        FileCvRepository cvRepository = new FileCvRepository();

        StudentController studentController = new StudentController(
                new StudentService(studentRepository, cvRepository, new StudentValidator())
        );
        JobController jobController = new JobController(new JobService(jobRepository, new JobValidator()));
        ApplicationController applicationController = new ApplicationController(
                new ApplicationService(
                        applicationRepository,
                        studentRepository,
                        jobRepository,
                        new ApplicationValidator()
                )
        );
        AuthController authController = new AuthController(new AuthService(userRepository, new AuthValidator()));

        return new MainFrame(authController, studentController, jobController, applicationController);
    }
}
