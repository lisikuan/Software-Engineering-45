package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.persistence.repository.ApplicationRepository;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: persist Application entities to data/applications.json using
 * the current minimal schema {id, studentId, jobId, status}.
 *
 * Current baseline:
 * - id means the internal application record identifier and is the persistence key.
 * - studentId and jobId are business association fields, not a composite key.
 * - data/applications.json currently stores only id, studentId, jobId, status.
 * - the minimal confirmed status set is SUBMITTED, APPROVED, REJECTED.
 * - update throws DataAccessException when the target id does not exist.
 * - duplicate application checks for the same studentId and jobId should be
 *   handled later in service, not hardcoded here.
 * - JSON field order is not treated as a strong contract.
 * - [待确认] Additional fields such as submittedAt, reviewedAt, comments,
 *   reference existence validation, and additional query methods.
 */
public class JsonApplicationRepository extends AbstractJsonRepository<Application> implements ApplicationRepository {
    private static final String FILE_NAME = "applications.json";

    public JsonApplicationRepository(JsonDataStore jsonDataStore) {
        super(jsonDataStore, FILE_NAME, Application.class);
    }

    @Override
    public List<Application> findAll() throws DataAccessException {
        return readAll();
    }

    @Override
    public Optional<Application> findById(String id) throws DataAccessException {
        return findExistingById(id);
    }

    @Override
    public Application insert(Application application) throws DataAccessException {
        return insertEntity(application);
    }

    @Override
    public Application update(Application application) throws DataAccessException {
        return updateEntity(application);
    }

    @Override
    public boolean deleteById(String id) throws DataAccessException {
        return deleteEntityById(id);
    }

    @Override
    protected String getId(Application entity) {
        return entity.getId();
    }
}