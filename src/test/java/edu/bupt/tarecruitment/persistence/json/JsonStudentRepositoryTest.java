package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.JsonFormatException;
import edu.bupt.tarecruitment.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
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
        Student student = new Student("S001", "Alice", "U001", "20230001", List.of("Java", "SQL"), "cvs/S001.pdf");

        repository.insert(student);

        String json = Files.readString(tempDir.resolve("students.json"));
        assertTrue(json.contains("\"id\" : \"S001\""));
        assertTrue(json.contains("\"name\" : \"Alice\""));
        assertTrue(json.contains("\"studentNumber\" : \"20230001\""));
        assertTrue(json.contains("\"cvFilePath\" : \"cvs/S001.pdf\""));
    }

    @Test
    void insertThrowsWhenStudentIdAlreadyExists() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("""
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001",
                    "studentNumber": "20230001",
                    "skillTags": ["Java"],
                    "cvFilePath": "cvs/S001.pdf"
                  }
                ]
                """);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> repository.insert(new Student("S001", "Bob", "U002", "20230002", List.of("Python"), null))
        );

        assertTrue(exception.getMessage().contains("Duplicate id 'S001'"));
    }

    @Test
    void updateReplacesExistingStudentInStudentsJson() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("""
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001",
                    "studentNumber": "20230001",
                    "skillTags": ["Java"],
                    "cvFilePath": "cvs/S001.pdf"
                  }
                ]
                """);

        repository.update(new Student("S001", "Alice Updated", "U001", "20230009", List.of("Java", "SQL"), "cvs/S001.pdf"));

        Student storedStudent = repository.findById("S001").orElseThrow();
        assertEquals("Alice Updated", storedStudent.getName());
        assertEquals("20230009", storedStudent.getStudentNumber());
        assertEquals(List.of("Java", "SQL"), storedStudent.getSkillTags());
    }

    @Test
    void deleteByIdReturnsTrueThenFalseForSameStudentId() throws Exception {
        JsonStudentRepository repository = createRepositoryWithStudentsJson("""
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001",
                    "studentNumber": "20230001",
                    "skillTags": ["Java"],
                    "cvFilePath": "cvs/S001.pdf"
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
}
