package edu.bupt.tarecruitment.persistence.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.bupt.tarecruitment.common.DateUtil;
import edu.bupt.tarecruitment.common.FileUtil;
import edu.bupt.tarecruitment.common.ValidationUtil;
import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.persistence.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JsonUserRepository 实现
 * 使用JSON文件进行用户数据持久化
 */
public class JsonUserRepository implements UserRepository {
    private static final String DATA_FILE = "data/users.json";
    private final ObjectMapper objectMapper;

    public JsonUserRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public User create(User user) {
        ValidationUtil.validateNotNull(user, "user");
        ValidationUtil.validateNotBlank(user.getUsername(), "username");
        ValidationUtil.validateNotBlank(user.getPassword(), "password");
        ValidationUtil.validateEmail(user.getEmail());

        if (existsByUsername(user.getUsername())) {
            throw new BusinessException("Username already exists: " + user.getUsername());
        }

        try {
            List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);

            if (user.getId() == null || user.getId().isEmpty()) {
                user.setId("user_" + UUID.randomUUID().toString().substring(0, 8));
            }

            if (user.getCreatedAt() == null) {
                user.setCreatedAt(DateUtil.now());
            }

            users.add(user);
            FileUtil.writeJsonArray(DATA_FILE, users);

            return user;
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to create user: " + e.getMessage(),
                DATA_FILE,
                "CREATE",
                e
            );
        }
    }

    @Override
    public Optional<User> findById(String id) {
        ValidationUtil.validateId(id);

        try {
            List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
            return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to find user by ID: " + e.getMessage(),
                DATA_FILE,
                "FIND_BY_ID",
                e
            );
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        ValidationUtil.validateNotBlank(username, "username");

        try {
            List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
            return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to find user by username: " + e.getMessage(),
                DATA_FILE,
                "FIND_BY_USERNAME",
                e
            );
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return FileUtil.readJsonArray(DATA_FILE, User.class);
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to find all users: " + e.getMessage(),
                DATA_FILE,
                "FIND_ALL",
                e
            );
        }
    }

    @Override
    public List<User> findByRole(User.UserRole role) {
        ValidationUtil.validateNotNull(role, "role");

        try {
            List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
            return users.stream()
                .filter(u -> u.getRole() == role)
                .toList();
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to find users by role: " + e.getMessage(),
                DATA_FILE,
                "FIND_BY_ROLE",
                e
            );
        }
    }

    @Override
    public User update(User user) {
        ValidationUtil.validateNotNull(user, "user");
        ValidationUtil.validateId(user.getId());

        try {
            List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);

            boolean found = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(user.getId())) {
                    users.set(i, user);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new BusinessException("User not found: " + user.getId());
            }

            FileUtil.writeJsonArray(DATA_FILE, users);
            return user;
        } catch (BusinessException | DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to update user: " + e.getMessage(),
                DATA_FILE,
                "UPDATE",
                e
            );
        }
    }

    @Override
    public boolean delete(String id) {
        ValidationUtil.validateId(id);

        try {
            List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
            boolean removed = users.removeIf(u -> u.getId().equals(id));

            if (removed) {
                FileUtil.writeJsonArray(DATA_FILE, users);
            }

            return removed;
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to delete user: " + e.getMessage(),
                DATA_FILE,
                "DELETE",
                e
            );
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try {
            return findByUsername(username).isPresent();
        } catch (DataAccessException e) {
            throw e;
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            return findById(id).isPresent();
        } catch (DataAccessException e) {
            throw e;
        }
    }

    @Override
    public int count() {
        try {
            return FileUtil.readJsonArray(DATA_FILE, User.class).size();
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(
                "Failed to count users: " + e.getMessage(),
                DATA_FILE,
                "COUNT",
                e
            );
        }
    }
}