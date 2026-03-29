package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonUserRepository 测试类
 * 测试用户持久化层的所有功能
 */
@DisplayName("JsonUserRepository Tests")
public class JsonUserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new JsonUserRepository();
    }

    @Test
    @DisplayName("应该能够创建新用户")
    void testCreateUser() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("test@example.com");

        User created = userRepository.create(newUser);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("testuser", created.getUsername());
        assertEquals(User.UserRole.STUDENT, created.getRole());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    @DisplayName("创建用户时用户名为空应该抛出异常")
    void testCreateUserWithBlankUsername() {
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("test@example.com");

        assertThrows(ValidationException.class, () -> userRepository.create(newUser));
    }

    @Test
    @DisplayName("创建用户时邮箱格式不正确应该抛出异常")
    void testCreateUserWithInvalidEmail() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("invalid-email");

        assertThrows(ValidationException.class, () -> userRepository.create(newUser));
    }

    @Test
    @DisplayName("创建重复用户名应该抛出异常")
    void testCreateDuplicateUsername() {
        User user1 = new User();
        user1.setUsername("duplicate");
        user1.setPassword("password123");
        user1.setRole(User.UserRole.STUDENT);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setUsername("duplicate");
        user2.setPassword("password456");
        user2.setRole(User.UserRole.STUDENT);
        user2.setEmail("user2@example.com");

        userRepository.create(user1);
        assertThrows(BusinessException.class, () -> userRepository.create(user2));
    }

    @Test
    @DisplayName("应该能够根据ID查询用户")
    void testFindById() {
        User newUser = new User();
        newUser.setUsername("findtest");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("find@example.com");
        User created = userRepository.create(newUser);

        Optional<User> found = userRepository.findById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
        assertEquals("findtest", found.get().getUsername());
    }

    @Test
    @DisplayName("查询不存在的用户应该返回空Optional")
    void testFindByIdNotFound() {
        Optional<User> found = userRepository.findById("nonexistent_id");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("应该能够根据用户名查询用户")
    void testFindByUsername() {
        User newUser = new User();
        newUser.setUsername("usernametest");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("username@example.com");
        userRepository.create(newUser);

        Optional<User> found = userRepository.findByUsername("usernametest");

        assertTrue(found.isPresent());
        assertEquals("usernametest", found.get().getUsername());
    }

    @Test
    @DisplayName("应该能够获取所有用户")
    void testFindAll() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password123");
        user1.setRole(User.UserRole.STUDENT);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password456");
        user2.setRole(User.UserRole.ADMIN);
        user2.setEmail("user2@example.com");

        userRepository.create(user1);
        userRepository.create(user2);

        List<User> users = userRepository.findAll();
        assertTrue(users.size() >= 2);
    }

    @Test
    @DisplayName("应该能够根据角色查询用户")
    void testFindByRole() {
        User studentUser = new User();
        studentUser.setUsername("student");
        studentUser.setPassword("password123");
        studentUser.setRole(User.UserRole.STUDENT);
        studentUser.setEmail("student@example.com");
        userRepository.create(studentUser);

        List<User> students = userRepository.findByRole(User.UserRole.STUDENT);
        assertTrue(students.size() > 0);
        assertTrue(students.stream().allMatch(u -> u.getRole() == User.UserRole.STUDENT));
    }

    @Test
    @DisplayName("应该能够更新用户信息")
    void testUpdateUser() {
        User newUser = new User();
        newUser.setUsername("updatetest");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("update@example.com");
        User created = userRepository.create(newUser);

        created.setEmail("updated@example.com");
        User updated = userRepository.update(created);

        assertEquals("updated@example.com", updated.getEmail());
        Optional<User> verified = userRepository.findById(created.getId());
        assertTrue(verified.isPresent());
        assertEquals("updated@example.com", verified.get().getEmail());
    }

    @Test
    @DisplayName("更新不存在的用户应该抛出异常")
    void testUpdateNonexistentUser() {
        User nonexistentUser = new User();
        nonexistentUser.setId("nonexistent_id");
        nonexistentUser.setUsername("test");
        nonexistentUser.setPassword("password");
        nonexistentUser.setRole(User.UserRole.STUDENT);
        nonexistentUser.setEmail("test@example.com");

        assertThrows(BusinessException.class, () -> userRepository.update(nonexistentUser));
    }

    @Test
    @DisplayName("应该能够删除用户")
    void testDeleteUser() {
        User newUser = new User();
        newUser.setUsername("deletetest");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("delete@example.com");
        User created = userRepository.create(newUser);

        boolean deleted = userRepository.delete(created.getId());

        assertTrue(deleted);
        Optional<User> found = userRepository.findById(created.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("删除不存在的用户应该返回false")
    void testDeleteNonexistentUser() {
        boolean deleted = userRepository.delete("nonexistent_id");
        assertFalse(deleted);
    }

    @Test
    @DisplayName("应该能够检查用户名是否存在")
    void testExistsByUsername() {
        User newUser = new User();
        newUser.setUsername("existtest");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("exist@example.com");
        userRepository.create(newUser);

        assertTrue(userRepository.existsByUsername("existtest"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("应该能够检查用户ID是否存在")
    void testExistsById() {
        User newUser = new User();
        newUser.setUsername("idtest");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("id@example.com");
        User created = userRepository.create(newUser);

        assertTrue(userRepository.existsById(created.getId()));
        assertFalse(userRepository.existsById("nonexistent_id"));
    }

    @Test
    @DisplayName("应该能够获取用户总数")
    void testCount() {
        int initialCount = userRepository.count();

        User newUser = new User();
        newUser.setUsername("counttest");
        newUser.setPassword("password123");
        newUser.setRole(User.UserRole.STUDENT);
        newUser.setEmail("count@example.com");
        userRepository.create(newUser);

        int finalCount = userRepository.count();
        assertEquals(initialCount + 1, finalCount);
    }
}