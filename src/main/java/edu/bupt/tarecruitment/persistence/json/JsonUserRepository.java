package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.DateUtil;
import edu.bupt.tarecruitment.common.FileUtil;
import edu.bupt.tarecruitment.common.ValidationUtil;
import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import edu.bupt.tarecruitment.persistence.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Responsibility: persist User entities to data/users.json.
 */
public class JsonUserRepository implements UserRepository {
    private static final String DATA_FILE = "data/users.json";

    public JsonUserRepository() {
    }

    @Override
    public User insert(User user) {
        ValidationUtil.validateNotNull(user, "user");
        ValidationUtil.validateNotBlank(user.getUsername(), "username");
        ValidationUtil.validateNotBlank(user.getPassword(), "password");
        ValidationUtil.validateEmail(user.getEmail());

        if (existsByUsername(user.getUsername())) {
            throw new BusinessException("Username already exists: " + user.getUsername());
        }

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
    }

    @Override
    public Optional<User> findById(String id) {
        ValidationUtil.validateId(id);

        List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
        return users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        ValidationUtil.validateNotBlank(username, "username");

        List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst();
    }

    @Override
    public List<User> findAll() {
        return FileUtil.readJsonArray(DATA_FILE, User.class);
    }

    @Override
    public List<User> findByRole(UserRole role) {
        ValidationUtil.validateNotNull(role, "role");

        List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
        return users.stream()
            .filter(u -> u.getRole() == role)
            .toList();
    }

    @Override
    public User update(User user) {
        ValidationUtil.validateNotNull(user, "user");
        ValidationUtil.validateId(user.getId());

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
    }

    @Override
    public boolean deleteById(String id) {
        ValidationUtil.validateId(id);

        List<User> users = FileUtil.readJsonArray(DATA_FILE, User.class);
        boolean removed = users.removeIf(u -> u.getId().equals(id));

        if (removed) {
            FileUtil.writeJsonArray(DATA_FILE, users);
        }

        return removed;
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    @Override
    public int count() {
        return FileUtil.readJsonArray(DATA_FILE, User.class).size();
    }
}
