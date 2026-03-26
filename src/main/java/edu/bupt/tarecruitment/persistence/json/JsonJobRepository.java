package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: persist Job entities to data/jobs.json using the current
 * minimal schema {id, title, description}.
 *
 * Current baseline:
 * - id is the persistence key for Job.
 * - data/jobs.json currently stores only id, title, description.
 * - update throws DataAccessException when the target id does not exist.
 * - JSON field order is not treated as a strong contract.
 * - [待确认] Additional job fields such as course, teacher, quota, deadline,
 *   publisher, and status.
 */
public class JsonJobRepository extends AbstractJsonRepository<Job> implements JobRepository {
    private static final String FILE_NAME = "jobs.json";

    public JsonJobRepository(JsonDataStore jsonDataStore) {
        super(jsonDataStore, FILE_NAME, Job.class);
    }

    @Override
    public List<Job> findAll() throws DataAccessException {
        return readAll();
    }

    @Override
    public Optional<Job> findById(String id) throws DataAccessException {
        return findExistingById(id);
    }

    @Override
    public Job insert(Job job) throws DataAccessException {
        return insertEntity(job);
    }

    @Override
    public Job update(Job job) throws DataAccessException {
        return updateEntity(job);
    }

    @Override
    public boolean deleteById(String id) throws DataAccessException {
        return deleteEntityById(id);
    }

    @Override
    protected String getId(Job entity) {
        return entity.getId();
    }
}