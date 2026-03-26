package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: persist Student entities to data/students.json using the
 * current minimal schema {id, name, userId}.
 *
 * Current baseline:
 * - id means the student number/student identifier and is the persistence key.
 * - userId references User.id.
 * - data/students.json currently stores only id, name, userId.
 * - update throws DataAccessException when the target id does not exist.
 * - JSON field order is not treated as a strong contract.
 * - [待确认] Whether persistence must also enforce userId uniqueness.
 */
public class JsonStudentRepository extends AbstractJsonRepository<Student> implements StudentRepository {
    private static final String FILE_NAME = "students.json";

    public JsonStudentRepository(JsonDataStore jsonDataStore) {
        super(jsonDataStore, FILE_NAME, Student.class);
    }

    @Override
    public List<Student> findAll() throws DataAccessException {
        return readAll();
    }

    @Override
    public Optional<Student> findById(String id) throws DataAccessException {
        return findExistingById(id);
    }

    @Override
    public Student insert(Student student) throws DataAccessException {
        return insertEntity(student);
    }

    @Override
    public Student update(Student student) throws DataAccessException {
        return updateEntity(student);
    }

    @Override
    public boolean deleteById(String id) throws DataAccessException {
        return deleteEntityById(id);
    }

    @Override
    protected String getId(Student entity) {
        return entity.getId();
    }
}