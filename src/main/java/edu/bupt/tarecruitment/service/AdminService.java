package edu.bupt.tarecruitment.service;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ConsistencyIssue;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import edu.bupt.tarecruitment.persistence.repository.ApplicationRepository;
import edu.bupt.tarecruitment.persistence.repository.JobRepository;
import edu.bupt.tarecruitment.persistence.repository.StudentRepository;
import edu.bupt.tarecruitment.persistence.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Responsibility: provide Admin-only application services such as account
 * creation and read-only data consistency checks. Business rules stay here
 * rather than in JSON repositories.
 */
public class AdminService {
    private static final String USER_ID_PREFIX = "U";

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public AdminService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<User> getAllUsers() throws DataAccessException {
        return userRepository.findAll();
    }

    public User createUser(String username, String password, UserRole role)
            throws ValidationException, BusinessException, DataAccessException {
        validateUserCreationInput(username, password, role);
        List<User> users = userRepository.findAll();
        boolean duplicateUsername = users.stream()
                .anyMatch(user -> username.trim().equals(user.getUsername()));
        if (duplicateUsername) {
            throw new BusinessException("Username already exists: " + username.trim());
        }

        User user = new User(generateNextUserId(users), username.trim(), password, role);
        return userRepository.insert(user);
    }

    public List<ConsistencyIssue> getConsistencyIssues() throws DataAccessException {
        List<User> users = userRepository.findAll();
        List<Student> students = studentRepository.findAll();
        List<Job> jobs = jobRepository.findAll();
        List<Application> applications = applicationRepository.findAll();

        List<ConsistencyIssue> issues = new java.util.ArrayList<>();
        addDuplicateIdIssues(issues, "User", users.stream().map(User::getId).toList());
        addDuplicateIdIssues(issues, "Student", students.stream().map(Student::getId).toList());
        addDuplicateIdIssues(issues, "Job", jobs.stream().map(Job::getId).toList());
        addDuplicateIdIssues(issues, "Application", applications.stream().map(Application::getId).toList());

        Set<String> userIds = new HashSet<>(users.stream().map(User::getId).toList());
        Set<String> studentIds = new HashSet<>(students.stream().map(Student::getId).toList());
        Set<String> jobIds = new HashSet<>(jobs.stream().map(Job::getId).toList());
        Set<String> moUserIds = new HashSet<>(users.stream()
                .filter(user -> user.getRole() == UserRole.MO)
                .map(User::getId)
                .toList());

        for (User user : users) {
            addBlankIssue(issues, "User", user.getId(), "username", user.getUsername());
            addBlankIssue(issues, "User", user.getId(), "password", user.getPassword());
            if (user.getRole() == null) {
                issues.add(new ConsistencyIssue("ERROR", "User", "User " + user.getId() + " has null role."));
            }
        }

        for (Student student : students) {
            addBlankIssue(issues, "Student", student.getId(), "name", student.getName());
            if (isBlank(student.getUserId())) {
                issues.add(new ConsistencyIssue("ERROR", "Student", "Student " + student.getId() + " has blank userId."));
            } else if (!userIds.contains(student.getUserId())) {
                issues.add(new ConsistencyIssue("ERROR", "Student", "Student " + student.getId()
                        + " references missing userId " + student.getUserId() + "."));
            }
        }

        for (Job job : jobs) {
            addBlankIssue(issues, "Job", job.getId(), "courseName", job.getCourseName());
            if (isBlank(job.getPublisherId())) {
                issues.add(new ConsistencyIssue("WARNING", "Job", "Job " + job.getId() + " has blank publisherId."));
            } else if (!userIds.contains(job.getPublisherId())) {
                issues.add(new ConsistencyIssue("ERROR", "Job", "Job " + job.getId()
                        + " references missing publisherId " + job.getPublisherId() + "."));
            } else if (!moUserIds.contains(job.getPublisherId())) {
                issues.add(new ConsistencyIssue("WARNING", "Job", "Job " + job.getId()
                        + " publisherId " + job.getPublisherId() + " is not an MO user."));
            }
        }

        for (Application application : applications) {
            if (isBlank(application.getStudentId())) {
                issues.add(new ConsistencyIssue("ERROR", "Application", "Application "
                        + application.getId() + " has blank studentId."));
            } else if (!studentIds.contains(application.getStudentId())) {
                issues.add(new ConsistencyIssue("ERROR", "Application", "Application "
                        + application.getId() + " references missing studentId " + application.getStudentId() + "."));
            }
            if (isBlank(application.getJobId())) {
                issues.add(new ConsistencyIssue("ERROR", "Application", "Application "
                        + application.getId() + " has blank jobId."));
            } else if (!jobIds.contains(application.getJobId())) {
                issues.add(new ConsistencyIssue("ERROR", "Application", "Application "
                        + application.getId() + " references missing jobId " + application.getJobId() + "."));
            }
            if (application.getStatus() == null) {
                issues.add(new ConsistencyIssue("ERROR", "Application", "Application "
                        + application.getId() + " has null status."));
            }
        }

        return issues;
    }

    private void validateUserCreationInput(String username, String password, UserRole role) throws ValidationException {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Username must not be blank.");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("Password must not be blank.");
        }
        if (role == null) {
            throw new ValidationException("User role must not be null.");
        }
        if (role == UserRole.ADMIN) {
            throw new ValidationException("Admin dashboard can create TA or MO accounts only.");
        }
    }

    private String generateNextUserId(List<User> users) {
        int maxNumber = users.stream()
                .map(User::getId)
                .filter(id -> id != null && id.matches(USER_ID_PREFIX + "\\d+"))
                .mapToInt(id -> Integer.parseInt(id.substring(USER_ID_PREFIX.length())))
                .max()
                .orElse(0);
        return USER_ID_PREFIX + String.format("%03d", maxNumber + 1);
    }

    private void addDuplicateIdIssues(List<ConsistencyIssue> issues, String category, List<String> ids) {
        Set<String> seen = new HashSet<>();
        Set<String> duplicateIds = new HashSet<>();
        for (String id : ids) {
            if (isBlank(id)) {
                issues.add(new ConsistencyIssue("ERROR", category, category + " record has blank id."));
            } else if (!seen.add(id)) {
                duplicateIds.add(id);
            }
        }
        for (String duplicateId : duplicateIds) {
            issues.add(new ConsistencyIssue("ERROR", category, category + " has duplicate id " + duplicateId + "."));
        }
    }

    private void addBlankIssue(List<ConsistencyIssue> issues, String category, String id, String fieldName, String value) {
        if (isBlank(value)) {
            issues.add(new ConsistencyIssue("ERROR", category, category + " " + id + " has blank " + fieldName + "."));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
