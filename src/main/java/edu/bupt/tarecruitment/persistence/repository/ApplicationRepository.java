package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.Application;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for Application
 * entities while keeping status rules and review logic out of the DAO layer.
 *
 * Current baseline:
 * - id means the internal application record identifier and is the repository key.
 * - studentId and jobId are business association fields, not a composite key.
 * - the current minimal field set is id, studentId, jobId, status only.
 * - the minimal confirmed status set is SUBMITTED, APPROVED, REJECTED.
 * - update must throw DataAccessException when the target id does not exist.
 * - interface naming remains unchanged for now.
 * - duplicate application checks for the same studentId and jobId should be
 *   handled later in service, not hardcoded here.
 * - [待确认] Whether additional lookup methods are needed for studentId,
 *   jobId, or application status.
 */
public interface ApplicationRepository {
    List<Application> findAll() throws DataAccessException;

    Optional<Application> findById(String id) throws DataAccessException;

    Application insert(Application application) throws DataAccessException;

    Application update(Application application) throws DataAccessException;

    boolean deleteById(String id) throws DataAccessException;
}