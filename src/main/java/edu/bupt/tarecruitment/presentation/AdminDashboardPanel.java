package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.AdminController;
import edu.bupt.tarecruitment.controller.AiController;
import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ConsistencyIssue;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;
import edu.bupt.tarecruitment.model.WorkloadReport;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private static final String VIEW_USERS = "users";
    private static final String VIEW_WORKLOAD = "workload";
    private static final String VIEW_RECORDS = "records";
    private static final String VIEW_CHECKS = "checks";

    private final User currentUser;
    private final AdminController adminController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final AiController aiController;
    private final Runnable logoutAction;

    private final DefaultTableModel usersModel;
    private final DefaultTableModel workloadModel;
    private final DefaultTableModel jobsModel;
    private final DefaultTableModel applicationsModel;
    private final DefaultTableModel issuesModel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<UserRole> roleBox;
    private final DashboardShell.StatCard userCountCard;
    private final DashboardShell.StatCard taCountCard;
    private final DashboardShell.StatCard moCountCard;
    private final DashboardShell.StatCard issueCountCard;

    private String currentView;

    public AdminDashboardPanel(
            User currentUser,
            AdminController adminController,
            JobController jobController,
            ApplicationController applicationController,
            AiController aiController,
            Runnable logoutAction
    ) {
        this.currentUser = currentUser;
        this.adminController = adminController;
        this.jobController = jobController;
        this.applicationController = applicationController;
        this.aiController = aiController;
        this.logoutAction = logoutAction;
        this.usersModel = readOnlyModel("User ID", "Username", "Role");
        this.workloadModel = readOnlyModel("TA ID", "Name", "Approved Jobs", "Weekly Hours", "Risk");
        this.jobsModel = readOnlyModel("Job ID", "Course", "Skills", "Hours", "Quota", "Status", "Publisher");
        this.applicationsModel = readOnlyModel("Application ID", "Student ID", "Job ID", "Status");
        this.issuesModel = readOnlyModel("Severity", "Category", "Message");
        this.usernameField = new JTextField(18);
        this.passwordField = new JPasswordField(18);
        this.roleBox = new JComboBox<>(new UserRole[]{UserRole.TA, UserRole.MO});
        this.userCountCard = DashboardShell.statCard("Total Users", "0");
        this.taCountCard = DashboardShell.statCard("TA Accounts", "0");
        this.moCountCard = DashboardShell.statCard("MO Accounts", "0");
        this.issueCountCard = DashboardShell.statCard("Data Issues", "0");
        this.currentView = VIEW_USERS;
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.PAGE_BG);
        UiTheme.styleField(usernameField);
        UiTheme.styleField(passwordField);
        UiTheme.styleComboBox(roleBox);
        renderView();
    }

    private void renderView() {
        removeAll();
        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setOpaque(false);
        content.add(DashboardShell.buildStatsRow(userCountCard, taCountCard, moCountCard, issueCountCard), BorderLayout.NORTH);
        content.add(buildBodyForCurrentView(), BorderLayout.CENTER);

        add(DashboardShell.buildShell(
                "Admin Portal",
                List.of(
                        new DashboardShell.NavItem("01", "Users", VIEW_USERS.equals(currentView), () -> switchView(VIEW_USERS)),
                        new DashboardShell.NavItem("02", "TA Workload", VIEW_WORKLOAD.equals(currentView), () -> switchView(VIEW_WORKLOAD)),
                        new DashboardShell.NavItem("03", "Records", VIEW_RECORDS.equals(currentView), () -> switchView(VIEW_RECORDS)),
                        new DashboardShell.NavItem("04", "Data Check", VIEW_CHECKS.equals(currentView), () -> switchView(VIEW_CHECKS)),
                        new DashboardShell.NavItem("05", "Logout", false, logoutAction)
                ),
                currentUser.getUsername(),
                "Administrator",
                viewTitle(),
                viewSubtitle(),
                content
        ), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel buildBodyForCurrentView() {
        return switch (currentView) {
            case VIEW_WORKLOAD -> tableSection("All TA Workload", "Approved workload across all TA profiles.", workloadModel);
            case VIEW_RECORDS -> buildRecordsView();
            case VIEW_CHECKS -> tableSection("Data Consistency Issues", "Reports only; no automatic repair is performed.", issuesModel);
            default -> buildUsersView();
        };
    }

    private JPanel buildUsersView() {
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tableSection("All Users", "All accounts currently stored in users.json.", usersModel),
                buildCreateUserPanel()
        );
        splitPane.setResizeWeight(0.68);
        UiTheme.styleSplitPane(splitPane);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(splitPane, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildCreateUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        UiTheme.styleSection(panel);
        panel.add(UiTheme.sectionTitle("Create TA / MO Account"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(7, 0, 7, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.gridx = 0;

        addFormRow(form, constraints, 0, "Username", usernameField);
        addFormRow(form, constraints, 2, "Password", passwordField);
        addFormRow(form, constraints, 4, "Role", roleBox);

        JButton createButton = new JButton("Create Account");
        UiTheme.stylePrimaryButton(createButton);
        createButton.addActionListener(event -> createUser());
        constraints.gridy = 6;
        form.add(createButton, constraints);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRecordsView() {
        JPanel records = new JPanel(new GridLayout(1, 2, 18, 0));
        records.setOpaque(false);
        records.add(tableSection("All Jobs", "Every job currently stored in jobs.json.", jobsModel));
        records.add(tableSection("All Applications", "Every application currently stored in applications.json.", applicationsModel));
        return records;
    }

    private JPanel tableSection(String title, String subtitle, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        UiTheme.styleSection(panel);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(UiTheme.sectionTitle(title));
        heading.add(Box.createVerticalStrut(4));
        heading.add(UiTheme.mutedLabel(subtitle));

        JTable table = new JTable(model);
        UiTheme.styleTable(table);
        JScrollPane scrollPane = UiTheme.styleScrollPane(new JScrollPane(table));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void addFormRow(JPanel form, GridBagConstraints constraints, int row, String label, java.awt.Component field) {
        constraints.gridy = row;
        form.add(label(label), constraints);
        constraints.gridy = row + 1;
        form.add(field, constraints);
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    private void switchView(String view) {
        currentView = view;
        refreshData();
        renderView();
    }

    private void refreshData() {
        try {
            refreshUsers();
            refreshWorkload();
            refreshJobsAndApplications();
            refreshIssues();
        } catch (DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshUsers() throws DataAccessException {
        List<User> users = adminController.getAllUsers();
        usersModel.setRowCount(0);
        for (User user : users) {
            usersModel.addRow(new Object[]{user.getId(), user.getUsername(), user.getRole()});
        }
        userCountCard.setValue(String.valueOf(users.size()));
        taCountCard.setValue(String.valueOf(users.stream().filter(user -> user.getRole() == UserRole.TA).count()));
        moCountCard.setValue(String.valueOf(users.stream().filter(user -> user.getRole() == UserRole.MO).count()));
    }

    private void refreshWorkload() throws DataAccessException {
        workloadModel.setRowCount(0);
        if (aiController == null) {
            return;
        }
        for (WorkloadReport report : aiController.getWorkloadReport()) {
            workloadModel.addRow(new Object[]{
                    report.getStudentId(),
                    report.getStudentName(),
                    report.getApprovedJobCount(),
                    report.getTotalWeeklyHours(),
                    report.isOverloaded() ? "OVERLOADED" : "OK"
            });
        }
    }

    private void refreshJobsAndApplications() throws DataAccessException {
        jobsModel.setRowCount(0);
        for (Job job : jobController.getAllJobs()) {
            jobsModel.addRow(new Object[]{
                    job.getId(),
                    displayCourse(job),
                    String.join(", ", job.getRequiredSkills()),
                    job.getWeeklyHours(),
                    job.getQuota(),
                    job.getStatus(),
                    job.getPublisherId()
            });
        }

        applicationsModel.setRowCount(0);
        for (Application application : applicationController.getAllApplications()) {
            applicationsModel.addRow(new Object[]{
                    application.getId(),
                    application.getStudentId(),
                    application.getJobId(),
                    application.getStatus()
            });
        }
    }

    private void refreshIssues() throws DataAccessException {
        List<ConsistencyIssue> issues = adminController.getConsistencyIssues();
        issuesModel.setRowCount(0);
        if (issues.isEmpty()) {
            issuesModel.addRow(new Object[]{"OK", "All", "No issues found."});
        } else {
            for (ConsistencyIssue issue : issues) {
                issuesModel.addRow(new Object[]{issue.getSeverity(), issue.getCategory(), issue.getMessage()});
            }
        }
        issueCountCard.setValue(String.valueOf(issues.size()));
    }

    private void createUser() {
        try {
            adminController.createUser(
                    usernameField.getText(),
                    new String(passwordField.getPassword()),
                    (UserRole) roleBox.getSelectedItem()
            );
            usernameField.setText("");
            passwordField.setText("");
            refreshData();
            showInfo("Account created.");
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            showError(exception.getMessage());
        }
    }

    private DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private String displayCourse(Job job) {
        if (job.getCourseName() != null && !job.getCourseName().isBlank()) {
            return job.getCourseName();
        }
        return job.getTitle();
    }

    private String viewTitle() {
        return switch (currentView) {
            case VIEW_WORKLOAD -> "TA Workload";
            case VIEW_RECORDS -> "Jobs & Applications";
            case VIEW_CHECKS -> "Data Consistency";
            default -> "User Administration";
        };
    }

    private String viewSubtitle() {
        return switch (currentView) {
            case VIEW_WORKLOAD -> "Check overall TA workload across the programme.";
            case VIEW_RECORDS -> "Review global recruitment records without editing them.";
            case VIEW_CHECKS -> "Find missing references, blank keys, and suspicious ownership.";
            default -> "Create TA/MO accounts and review all users.";
        };
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Admin", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Admin Error", JOptionPane.ERROR_MESSAGE);
    }
}
