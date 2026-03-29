package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.JsonFormatException;
import edu.bupt.tarecruitment.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonStudentRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void findAllReturnsEmptyListWhenStudentsFileIsEmptyArray() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("[]");

        List<Student> students = repository.findAll();

        assertTrue(students.isEmpty());
    }

    @Test
    void insertWritesStudentToStudentsJson() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("[]");
        Student student = createStudent("S001", "U001", "Alice", "Smith", "2021001");

        repository.insert(student);

        String json = Files.readString(tempDir.resolve("students.json"));
        assertTrue(json.contains("\"id\" : \"S001\""));
        assertTrue(json.contains("\"firstName\" : \"Alice\""));
        assertTrue(json.contains("\"userId\" : \"U001\""));
    }

    @Test
    void insertThrowsWhenStudentIdAlreadyExists() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("""
                [
                  {
                    "id": "S001",
                    "userId": "U001",
                    "firstName": "Alice",
                    "lastName": "Smith",
                    "studentId": "2021001",
                    "email": "alice@example.com",
                    "phone": "1234567890",
                    "skills": [],
                    "createdAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> repository.insert(createStudent("S001", "U002", "Bob", "Jones", "2021002"))
        );

        assertTrue(exception.getMessage().contains("Duplicate id 'S001'"));
    }

    @Test
    void updateReplacesExistingStudentInStudentsJson() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("""
                [
                  {
                    "id": "S001",
                    "userId": "U001",
                    "firstName": "Alice",
                    "lastName": "Smith",
                    "studentId": "2021001",
                    "email": "alice@example.com",
                    "phone": "1234567890",
                    "skills": [],
                    "createdAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        Student updatedStudent = createStudent("S001", "U009", "Alice Updated", "Smith", "2021001");
        repository.update(updatedStudent);

        Student storedStudent = repository.findById("S001").orElseThrow();
        assertEquals("Alice Updated", storedStudent.getFirstName());
        assertEquals("U009", storedStudent.getUserId());
    }

    @Test
    void deleteByIdReturnsTrueThenFalseForSameStudentId() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("""
                [
                  {
                    "id": "S001",
                    "userId": "U001",
                    "firstName": "Alice",
                    "lastName": "Smith",
                    "studentId": "2021001",
                    "email": "alice@example.com",
                    "phone": "1234567890",
                    "skills": [],
                    "createdAt": "2024-01-01T00:00:00"
                  }
                ]
                """);

        boolean firstDelete = repository.deleteById("S001");
        boolean secondDelete = repository.deleteById("S001");

        assertTrue(firstDelete);
        assertFalse(secondDelete);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void readThrowsJsonFormatExceptionWhenStudentsJsonIsMalformed() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("{ invalid json");

        assertThrows(JsonFormatException.class, repository::findAll);
    }

    private JsonStudentRepository createRepositoryWithStudentsJson(String jsonContent) throws Exception {
        Files.writeString(tempDir.resolve("students.json"), jsonContent);
        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new JsonStudentRepository(jsonDataStore);
    }

    private Student createStudent(String id, String userId, String firstName, String lastName, String studentId) {
        return new Student(id, userId, firstName, lastName, studentId,
                firstName.toLowerCase() + "@example.com", "1234567890", null, List.of(), LocalDateTime.now());
    }
}
