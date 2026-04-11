package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.JsonFormatException;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUserRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void findAllReturnsEmptyListWhenUsersFileIsEmptyArray() throws Exception {
        JsonUserRepository repository = createRepositoryWithUsersJson("[]");

        List<User> users = repository.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void insertWritesUserToUsersJson() throws Exception {
        JsonUserRepository repository = createRepositoryWithUsersJson("[]");
        User user = new User("U001", "student1", "student123", UserRole.STUDENT);

        repository.insert(user);

        String json = Files.readString(tempDir.resolve("users.json"));
        assertTrue(json.contains("\"id\" : \"U001\""));
        assertTrue(json.contains("\"username\" : \"student1\""));
        assertTrue(json.contains("\"password\" : \"student123\""));
        assertTrue(json.contains("\"role\" : \"STUDENT\""));
    }

    @Test
    void updateReplacesExistingUserInUsersJson() throws Exception {
        JsonUserRepository repository = createRepositoryWithUsersJson("""
                [
                  {
                    "id": "U001",
                    "username": "student1",
                    "password": "student123",
                    "role": "STUDENT"
                  }
                ]
                """);

        repository.update(new User("U001", "student2", "newPass", UserRole.ADMIN));

        User storedUser = repository.findById("U001").orElseThrow();
        assertEquals("student2", storedUser.getUsername());
        assertEquals("newPass", storedUser.getPassword());
        assertEquals(UserRole.ADMIN, storedUser.getRole());
    }

    @Test
    void deleteByIdReturnsTrueThenFalseForSameUserId() throws Exception {
        JsonUserRepository repository = createRepositoryWithUsersJson("""
                [
                  {
                    "id": "U001",
                    "username": "student1",
                    "password": "student123",
                    "role": "STUDENT"
                  }
                ]
                """);

        boolean firstDelete = repository.deleteById("U001");
        boolean secondDelete = repository.deleteById("U001");

        assertTrue(firstDelete);
        assertFalse(secondDelete);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void readThrowsJsonFormatExceptionWhenUsersJsonIsMalformed() throws Exception {
        JsonUserRepository repository = createRepositoryWithUsersJson("{ invalid json");

        assertThrows(JsonFormatException.class, repository::findAll);
    }

    @Test
    void insertThrowsWhenUserIdAlreadyExists() throws Exception {
        JsonUserRepository repository = createRepositoryWithUsersJson("""
                [
                  {
                    "id": "U001",
                    "username": "student1",
                    "password": "student123",
                    "role": "STUDENT"
                  }
                ]
                """);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> repository.insert(new User("U001", "admin1", "admin123", UserRole.ADMIN))
        );

        assertTrue(exception.getMessage().contains("Duplicate id 'U001'"));
    }

    private JsonUserRepository createRepositoryWithUsersJson(String jsonContent) throws Exception {
        Files.writeString(tempDir.resolve("users.json"), jsonContent);
        JsonDataStore jsonDataStore = new JsonDataStore(tempDir);
        return new JsonUserRepository(jsonDataStore);
    }
}
