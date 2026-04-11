package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import edu.bupt.tarecruitment.persistence.json.JsonDataStore;
import edu.bupt.tarecruitment.persistence.json.JsonUserRepository;
import edu.bupt.tarecruitment.validation.AuthValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void loginReturnsUserWhenCredentialsAndRoleMatch() throws Exception {
        AuthService authService = createAuthService();

        User user = authService.login("student1", "student123", UserRole.STUDENT);

        assertEquals("U001", user.getId());
        assertEquals(UserRole.STUDENT, user.getRole());
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() throws Exception {
        AuthService authService = createAuthService();

        assertThrows(BusinessException.class, () -> authService.login("student1", "wrong", UserRole.STUDENT));
    }

    @Test
    void loginThrowsWhenRoleDoesNotMatch() throws Exception {
        AuthService authService = createAuthService();

        assertThrows(BusinessException.class, () -> authService.login("student1", "student123", UserRole.ADMIN));
    }

    private AuthService createAuthService() throws Exception {
        Files.writeString(tempDir.resolve("users.json"), """
                [
                  {
                    "id": "U001",
                    "username": "student1",
                    "password": "student123",
                    "role": "STUDENT"
                  },
                  {
                    "id": "U900",
                    "username": "admin1",
                    "password": "admin123",
                    "role": "ADMIN"
                  }
                ]
                """);
        JsonUserRepository userRepository = new JsonUserRepository(new JsonDataStore(tempDir));
        return new AuthService(userRepository, new AuthValidator());
    }
}
