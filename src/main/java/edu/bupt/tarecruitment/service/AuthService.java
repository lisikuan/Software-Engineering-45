package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import edu.bupt.tarecruitment.persistence.repository.UserRepository;
import edu.bupt.tarecruitment.validation.AuthValidator;

public class AuthService {
    private final UserRepository userRepository;
    private final AuthValidator authValidator;

    public AuthService(UserRepository userRepository, AuthValidator authValidator) {
        this.userRepository = userRepository;
        this.authValidator = authValidator;
    }

    public User login(String username, String password, UserRole role)
            throws ValidationException, BusinessException, DataAccessException {
        authValidator.validateLoginInput(username, password, role);

        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .filter(user -> user.getRole() == role)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Invalid username, password, or role."));
    }
}
