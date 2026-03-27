package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.Application;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    Application create(Application application);
    Optional<Application> findById(String id);
    List<Application> findByStudentId(String studentId);
    List<Application> findByJobId(String jobId);
    Optional<Application> findByJobIdAndStudentId(String jobId, String studentId);
    List<Application> findAll();
    List<Application> findByStatus(Application.ApplicationStatus status);
    Application update(Application application);
    boolean delete(String id);
    boolean existsById(String id);
    int count();
    int countByJobId(String jobId);
}