package edu.bupt.tarecruitment.controller;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.ConsistencyIssue;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import edu.bupt.tarecruitment.service.AdminService;

import java.util.List;

public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public List<User> getAllUsers() throws DataAccessException {
        return adminService.getAllUsers();
    }

    public User createUser(String username, String password, UserRole role)
            throws ValidationException, BusinessException, DataAccessException {
        return adminService.createUser(username, password, role);
    }

    public List<ConsistencyIssue> getConsistencyIssues() throws DataAccessException {
        return adminService.getConsistencyIssues();
    }
}
