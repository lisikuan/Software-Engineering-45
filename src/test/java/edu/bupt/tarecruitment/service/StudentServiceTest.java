package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.persistence.json.FileCvRepository;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonStudentRepository;
import edu.bupt.tarecruitment.validation.StudentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void saveProfileCreatesStudentAndStoresPdf() throws Exception {
        StudentService studentService = createStudentService("[]");
        Path cvFile = tempDir.resolve("alice-cv.pdf");
        Files.writeString(cvFile, "demo pdf");

        Student saved = studentService.saveProfile(
                "U001",
                "Alice",
                "2024001",
                List.of("Java", "Communication"),
                cvFile
        );

        assertEquals("S001", saved.getId());
        assertEquals("2024001", saved.getStudentNumber());
        assertEquals(List.of("Java", "Communication"), saved.getSkillTags());
        assertEquals("cvs/S001.pdf", saved.getCvFilePath());
        assertTrue(Files.exists(studentService.resolveCvFilePath(saved.getCvFilePath())));
    }

    @Test
    void saveProfileThrowsWhenStudentNumberBelongsToAnotherStudent() throws Exception {
        StudentService studentService = createStudentService("""
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001",
                    "studentNumber": "2024001",
                    "skillTags": ["Java"],
                    "cvFilePath": "cvs/S001.pdf"
                  }
                ]
                """);

        assertThrows(
                BusinessException.class,
                () -> studentService.saveProfile("U002", "Bob", "2024001", List.of("SQL"), null)
        );
    }

    @Test
    void saveProfileUpdatesExistingStudentWithoutChangingId() throws Exception {
        StudentService studentService = createStudentService("""
                [
                  {
                    "id": "S001",
                    "name": "Alice",
                    "userId": "U001",
                    "studentNumber": "2024001",
                    "skillTags": ["Java"],
                    "cvFilePath": "cvs/S001.pdf"
                  }
                ]
                """);

        Student updated = studentService.saveProfile("U001", "Alice Chen", "2024999", List.of("Java", "SQL"), null);

        assertEquals("S001", updated.getId());
        assertEquals("Alice Chen", updated.getName());
        assertEquals("2024999", updated.getStudentNumber());
        assertEquals(List.of("Java", "SQL"), updated.getSkillTags());
    }

    private StudentService createStudentService(String studentsJson) throws Exception {
        Files.writeString(tempDir.resolve("students.json"), studentsJson);
        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new StudentService(
                new JsonStudentRepository(jsonDataStore),
                new FileCvRepository(tempDir),
                new StudentValidator()
        );
    }
}
