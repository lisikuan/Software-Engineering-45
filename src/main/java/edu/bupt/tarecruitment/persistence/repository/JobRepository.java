package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.Job;
import java.util.List;
import java.util.Optional;

public interface JobRepository {
    Job create(Job job);
    Optional<Job> findById(String id);
    List<Job> findAll();
    List<Job> findByStatus(Job.JobStatus status);
    List<Job> findByCourseCode(String courseCode);
    Job update(Job job);
    boolean delete(String id);
    boolean existsById(String id);
    int count();
}