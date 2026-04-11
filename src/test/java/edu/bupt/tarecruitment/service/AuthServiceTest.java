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

        User user = authService.login("ta1", "ta123", UserRole.TA);

        assertEquals("U001", user.getId());
        assertEquals(UserRole.TA, user.getRole());
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() throws Exception {
        AuthService authService = createAuthService();

        assertThrows(BusinessException.class, () -> authService.login("ta1", "wrong", UserRole.TA));
    }

    @Test
    void loginThrowsWhenRoleDoesNotMatch() throws Exception {
        AuthService authService = createAuthService();

        assertThrows(BusinessException.class, () -> authService.login("ta1", "ta123", UserRole.MO));
    }

    private AuthService createAuthService() throws Exception {
        Files.writeString(tempDir.resolve("users.json"), """
                [
                  {
                    "id": "U001",
                    "username": "ta1",
                    "password": "ta123",
                    "role": "TA"
                  },
                  {
                    "id": "U900",
                    "username": "mo1",
                    "password": "mo123",
                    "role": "MO"
                  }
                ]
                """);
        JsonUserRepository userRepository = new JsonUserRepository(new JsonDataStore(tempDir));
        return new AuthService(userRepository, new AuthValidator());
    }
}