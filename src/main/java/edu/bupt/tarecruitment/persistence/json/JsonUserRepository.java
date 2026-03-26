package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.persistence.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: persist User entities to data/users.json using the current
 * minimal schema {id, username, role}. [待确认] Password storage strategy and
 * additional profile fields must be confirmed before expansion.
 */
public class JsonUserRepository extends AbstractJsonRepository<User> implements UserRepository {
    private static final String FILE_NAME = "users.json";

    public JsonUserRepository(JsonDataStore jsonDataStore) {
        super(jsonDataStore, FILE_NAME, User.class);
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        return readAll();
    }

    @Override
    public Optional<User> findById(String id) throws DataAccessException {
        return findExistingById(id);
    }

    @Override
    public User insert(User user) throws DataAccessException {
        return insertEntity(user);
    }

    @Override
    public User update(User user) throws DataAccessException {
        return updateEntity(user);
    }

    @Override
    public boolean deleteById(String id) throws DataAccessException {
        return deleteEntityById(id);
    }

    @Override
    protected String getId(User entity) {
        return entity.getId();
    }
}
