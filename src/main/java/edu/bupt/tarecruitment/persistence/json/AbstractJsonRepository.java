package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Responsibility: share generic JSON-backed CRUD mechanics across concrete
 * repositories while leaving entity-specific IDs and file names to subclasses.
 */
public abstract class AbstractJsonRepository<T> {
    private final JsonDataStore jsonDataStore;
    private final String fileName;
    private final Class<T> entityType;

    protected AbstractJsonRepository(JsonDataStore jsonDataStore, String fileName, Class<T> entityType) {
        this.jsonDataStore = jsonDataStore;
        this.fileName = fileName;
        this.entityType = entityType;
    }

    protected List<T> readAll() throws DataAccessException {
        return new ArrayList<>(jsonDataStore.readList(fileName, entityType));
    }

    protected Optional<T> findExistingById(String id) throws DataAccessException {
        return readAll().stream().filter(entity -> getId(entity).equals(id)).findFirst();
    }

    protected T insertEntity(T entity) throws DataAccessException {
        List<T> entities = readAll();
        String id = getId(entity);
        if (containsId(entities, id)) {
            throw new DataAccessException("Duplicate id '" + id + "' in file " + fileName);
        }
        entities.add(entity);
        jsonDataStore.writeList(fileName, entities);
        return entity;
    }

    protected T updateEntity(T entity) throws DataAccessException {
        List<T> entities = readAll();
        String id = getId(entity);
        int index = findIndexById(entities, id);
        if (index < 0) {
            throw new DataAccessException("Cannot update missing id '" + id + "' in file " + fileName);
        }
        entities.set(index, entity);
        jsonDataStore.writeList(fileName, entities);
        return entity;
    }

    protected boolean deleteEntityById(String id) throws DataAccessException {
        List<T> entities = readAll();
        boolean removed = entities.removeIf(entity -> getId(entity).equals(id));
        if (removed) {
            jsonDataStore.writeList(fileName, entities);
        }
        return removed;
    }

    private boolean containsId(List<T> entities, String id) {
        return entities.stream().anyMatch(entity -> getId(entity).equals(id));
    }

    private int findIndexById(List<T> entities, String id) {
        for (int index = 0; index < entities.size(); index++) {
            if (getId(entities.get(index)).equals(id)) {
                return index;
            }
        }
        return -1;
    }

    protected abstract String getId(T entity);
}
