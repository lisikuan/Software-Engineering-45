package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.Job;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for Job entities
 * without hardcoding any UI or business workflow behavior.
 *
 * Current baseline:
 * - id is the repository key for Job.
 * - the current minimal field set is id, title, description only.
 * - update must throw DataAccessException when the target id does not exist.
 * - interface naming remains unchanged for now.
 * - [待确认] Whether additional lookup methods are needed for course, teacher,
 *   publisher, or publish status.
 */
public interface JobRepository {
    List<Job> findAll() throws DataAccessException;

    Optional<Job> findById(String id) throws DataAccessException;

    Job insert(Job job) throws DataAccessException;

    Job update(Job job) throws DataAccessException;

    boolean deleteById(String id) throws DataAccessException;
}