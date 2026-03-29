package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.Job;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for Job entities
 * without hardcoding any UI or business workflow behavior.
 */
public interface JobRepository {
    List<Job> findAll();

    Optional<Job> findById(String id);

    Job insert(Job job);

    Job update(Job job);

    boolean deleteById(String id);
}
