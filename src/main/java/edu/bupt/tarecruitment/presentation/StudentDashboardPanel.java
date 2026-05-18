package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.controller.StudentController;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.User;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentDashboardPanel extends JPanel {
    private static final String VIEW_DASHBOARD = "dashboard";
    private static final String VIEW_APPLICATIONS = "applications";
    private static final String VIEW_PROFILE = "profile";

    private final User currentUser;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final Runnable logoutAction;
    private final DefaultTableModel jobsModel;
    private final DefaultTableModel applicationsModel;
    private final JTable jobsTable;
    private final JTable applicationsTable;
    private final JLabel profileStatusLabel;
    private final JTextField nameField;
    private final JTextField studentNumberField;
    private final JTextField majorField;
    private final JTextField gradeField;
    private final JTextField skillTagsField;
    private final JLabel cvPathLabel;
    private final DashboardShell.StatCard openJobsCard;
    private final DashboardShell.StatCard applicationsCard;
    private final DashboardShell.StatCard profileCard;

    private Student currentStudent;
    private Path selectedCvSourceFile;
    private String currentView;

    public StudentDashboardPanel(
            User currentUser,
            StudentController studentController,
            JobController jobController,
            ApplicationController applicationController,
            Runnable logoutAction
    ) {
        this.currentUser = currentUser;
        this.studentController = studentController;
        this.jobController = jobController;
        this.applicationController = applicationController;
        this.logoutAction = logoutAction;
        this.jobsModel = new DefaultTableModel(
                new Object[]{"Job ID", "Course", "Title", "Required Skills", "Missing Skills", "Weekly Hours", "Description"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.applicationsModel = new DefaultTableModel(
                new Object[]{"Application ID", "Job ID", "Course", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.jobsTable = new JTable(jobsModel);
        this.applicationsTable = new JTable(applicationsModel);
        this.profileStatusLabel = new JLabel();
        this.nameField = new JTextField(18);
        this.studentNumberField = new JTextField(18);
        this.majorField = new JTextField(18);
        this.gradeField = new JTextField(18);
        this.skillTagsField = new JTextField(18);
        this.cvPathLabel = new JLabel("No CV selected");
        this.openJobsCard = DashboardShell.statCard("Open Jobs", "0");
        this.applicationsCard = DashboardShell.statCard("My Applications", "0");
        this.profileCard = DashboardShell.statCard("Profile Status", "Pending");
        this.currentView = VIEW_DASHBOARD;
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.PAGE_BG);

        UiTheme.styleTable(jobsTable);
        UiTheme.styleTable(applicationsTable);
        UiTheme.styleField(nameField);
        UiTheme.styleField(studentNumberField);
        UiTheme.styleField(majorField);
        UiTheme.styleField(gradeField);
        UiTheme.styleField(skillTagsField);
        renderView();
    }

    private void renderView() {
        removeAll();

        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setOpaque(false);
        content.add(DashboardShell.buildStatsRow(openJobsCard, applicationsCard, profileCard), BorderLayout.NORTH);
        content.add(buildBodyForCurrentView(), BorderLayout.CENTER);

        add(DashboardShell.buildShell(
                "TA Workspace",
                List.of(
                        new DashboardShell.NavItem("01", "Dashboard", VIEW_DASHBOARD.equals(currentView), () -> switchView(VIEW_DASHBOARD)),
                        new DashboardShell.NavItem("02", "Applications", VIEW_APPLICATIONS.equals(currentView), () -> switchView(VIEW_APPLICATIONS)),
                        new DashboardShell.NavItem("03", "Profile", VIEW_PROFILE.equals(currentView), () -> switchView(VIEW_PROFILE)),
                        new DashboardShell.NavItem("04", "Logout", false, logoutAction)
                ),
                currentUser.getUsername(),
                "Teaching Assistant",
                viewTitle(),
                viewSubtitle(),
                content
        ), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JComponent buildBodyForCurrentView() {
        return switch (currentView) {
            case VIEW_APPLICATIONS -> buildApplicationsView();
            case VIEW_PROFILE -> buildProfileOnlyView();
            default -> buildDashboardView();
        };
    }

    private JSplitPane buildDashboardView() {
        JScrollPane profileScroll = new JScrollPane(buildProfileCard());
        profileScroll.setBorder(BorderFactory.createEmptyBorder());
        profileScroll.getViewport().setBackground(UiTheme.PAGE_BG);
        profileScroll.getVerticalScrollBar().setUnitIncrement(12);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, profileScroll, buildJobsCard());
        splitPane.setResizeWeight(0.40);
        UiTheme.styleSplitPane(splitPane);
        return splitPane;
    }

    private JPanel buildApplicationsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(buildJobsCard(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildProfileOnlyView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(buildProfilePageCard());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UiTheme.PAGE_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void switchView(String nextView) {
        if (nextView.equals(currentView)) {
            return;
        }
        currentView = nextView;
        renderView();
    }

    private String viewTitle() {
        return switch (currentView) {
            case VIEW_APPLICATIONS -> "Applications";
            case VIEW_PROFILE -> "Profile";
            default -> "TA Dashboard";
        };
    }

    private String viewSubtitle() {
        return switch (currentView) {
            case VIEW_APPLICATIONS -> "Browse open positions and monitor your submission results.";
            case VIEW_PROFILE -> "Maintain your profile, skills, and CV before applying.";
            default -> "Manage your profile, browse jobs, and track application progress.";
        };
    }

    private JPanel buildProfileCard() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        UiTheme.styleSection(wrapper);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.sectionTitle("Student Profile"), BorderLayout.WEST);
        header.add(UiTheme.badge("TA"), BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);

        profileStatusLabel.setFont(UiTheme.BODY_FONT);
        body.add(profileStatusLabel, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(compactFieldBlock("Name", nameField));
        form.add(Box.createVerticalStrut(10));
        form.add(compactFieldBlock("Student Number", studentNumberField));
        form.add(Box.createVerticalStrut(10));
        form.add(compactFieldBlock("Major", majorField));
        form.add(Box.createVerticalStrut(10));
        form.add(compactFieldBlock("Grade", gradeField));
        form.add(Box.createVerticalStrut(10));
        form.add(compactFieldBlock("Skill Tags", skillTagsField));
        form.add(Box.createVerticalStrut(10));
        form.add(compactFieldBlock("CV", buildCvInfoPanel()));
        form.add(Box.createVerticalStrut(10));
        JButton chooseCvButton = new JButton("Choose PDF CV");
        UiTheme.styleSecondaryButton(chooseCvButton);
        chooseCvButton.addActionListener(event -> chooseCvFile());
        form.add(chooseCvButton);
        form.add(Box.createVerticalStrut(10));
        JButton saveProfileButton = new JButton("Create / Update Profile");
        UiTheme.stylePrimaryButton(saveProfileButton);
        saveProfileButton.addActionListener(event -> saveProfile());
        form.add(saveProfileButton);

        body.add(form, BorderLayout.CENTER);
        wrapper.add(body, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildProfilePageCard() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        UiTheme.styleSection(wrapper);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.sectionTitle("Student Profile"), BorderLayout.WEST);
        header.add(UiTheme.badge("TA"), BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        profileStatusLabel.setFont(UiTheme.BODY_FONT);
        body.add(profileStatusLabel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 14, 12));
        grid.setOpaque(false);
        grid.add(fieldBlock("Name", nameField));
        grid.add(fieldBlock("Student Number", studentNumberField));
        grid.add(fieldBlock("Major", majorField));
        grid.add(fieldBlock("Grade", gradeField));
        body.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(fieldBlock("Skill Tags", skillTagsField));
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(fieldBlock("CV", buildCvInfoPanel()));
        bottom.add(Box.createVerticalStrut(12));

        JButton chooseCvButton = new JButton("Choose PDF CV");
        UiTheme.styleSecondaryButton(chooseCvButton);
        chooseCvButton.addActionListener(event -> chooseCvFile());
        bottom.add(chooseCvButton);
        bottom.add(Box.createVerticalStrut(12));

        JButton saveProfileButton = new JButton("Create / Update Profile");
        UiTheme.stylePrimaryButton(saveProfileButton);
        saveProfileButton.addActionListener(event -> saveProfile());
        bottom.add(saveProfileButton);

        body.add(bottom, BorderLayout.SOUTH);
        wrapper.add(body, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildCvInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        cvPathLabel.setFont(UiTheme.BODY_FONT);
        cvPathLabel.setForeground(UiTheme.MUTED);
        panel.add(cvPathLabel);
        return panel;
    }

    private JPanel buildJobsCard() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        UiTheme.styleSection(wrapper);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.sectionTitle("Jobs & Applications"), BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton submitButton = new JButton("Submit Selected Job");
        submitButton.addActionListener(event -> submitSelectedJob());
        UiTheme.stylePrimaryButton(submitButton);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        UiTheme.styleSecondaryButton(refreshButton);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(event -> logoutAction.run());
        UiTheme.styleDangerButton(logoutButton);
        actions.add(submitButton);
        actions.add(refreshButton);
        actions.add(logoutButton);
        header.add(actions, BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 14));
        center.setOpaque(false);
        center.add(buildTableSection("Open Positions", "Available roles aligned to your profile", jobsTable));
        center.add(buildTableSection("My Applications", "Track the latest review results", applicationsTable));

        wrapper.add(center, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    private JPanel fieldBlock(String text, JComponent component) {
        JPanel block = new JPanel(new BorderLayout(0, 8));
        block.setOpaque(false);
        block.add(fieldLabel(text), BorderLayout.NORTH);
        block.add(component, BorderLayout.CENTER);
        return block;
    }

    private JPanel compactFieldBlock(String text, JComponent component) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);
        block.add(fieldLabel(text), BorderLayout.NORTH);
        block.add(component, BorderLayout.CENTER);
        return block;
    }

    private JPanel buildTableSection(String title, String subtitle, JTable table) {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.LINE, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(UiTheme.sectionTitle(title));
        top.add(Box.createVerticalStrut(4));
        top.add(UiTheme.mutedLabel(subtitle));

        JScrollPane scrollPane = UiTheme.styleScrollPane(new JScrollPane(table));
        section.add(top, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);
        return section;
    }

    private void refreshData() {
        try {
            currentStudent = studentController.findStudentByUserId(currentUser.getId()).orElse(null);
            updateProfileForm();

            List<Job> jobs = jobController.getOpenJobs();
            openJobsCard.setValue(String.valueOf(jobs.size()));
            Map<String, String> jobCourses = new HashMap<>();
            jobsModel.setRowCount(0);
            for (Job job : jobs) {
                String missingSkills = currentStudent == null
                        ? "-"
                        : String.join(", ", studentController.getMissingSkills(currentStudent.getId(), job));
                jobsModel.addRow(new Object[]{
                        job.getId(),
                        valueOrPlaceholder(job.getCourseName()),
                        job.getTitle(),
                        String.join(", ", job.getRequiredSkills()),
                        missingSkills,
                        job.getWeeklyHours(),
                        job.getDescription()
                });
                jobCourses.put(job.getId(), valueOrPlaceholder(job.getCourseName()));
            }

            applicationsModel.setRowCount(0);
            if (currentStudent != null) {
                List<Application> applications = applicationController.getApplicationsForStudent(currentStudent.getId());
                applicationsCard.setValue(String.valueOf(applications.size()));
                for (Application application : applications) {
                    applicationsModel.addRow(new Object[]{
                            application.getId(),
                            application.getJobId(),
                            jobCourses.getOrDefault(application.getJobId(), "Unknown Course"),
                            application.getStatus().name()
                    });
                }
            } else {
                applicationsCard.setValue("0");
            }
        } catch (ValidationException | DataAccessException | BusinessException exception) {
            UiTheme.showError(this, "Refresh Failed", exception.getMessage());
        }
    }

    private void updateProfileForm() {
        if (currentStudent == null) {
            profileStatusLabel.setText("Profile status: not created yet.");
            profileStatusLabel.setForeground(UiTheme.WARNING);
            profileCard.setValue("Pending");
            nameField.setText("");
            studentNumberField.setText("");
            majorField.setText("");
            gradeField.setText("");
            skillTagsField.setText("");
            if (selectedCvSourceFile == null) {
                cvPathLabel.setText("No CV selected");
            }
            return;
        }

        profileStatusLabel.setText("Profile status: created. You can edit and save again.");
        profileStatusLabel.setForeground(UiTheme.SUCCESS);
        profileCard.setValue("Ready");
        nameField.setText(valueOrEmpty(currentStudent.getName()));
        studentNumberField.setText(valueOrEmpty(currentStudent.getStudentNumber()));
        majorField.setText(valueOrEmpty(currentStudent.getMajor()));
        gradeField.setText(valueOrEmpty(currentStudent.getGrade()));
        skillTagsField.setText(String.join(", ", currentStudent.getSkillTags()));
        if (selectedCvSourceFile != null) {
            cvPathLabel.setText(selectedCvSourceFile.getFileName().toString());
        } else if (currentStudent.getCvFilePath() != null && !currentStudent.getCvFilePath().isBlank()) {
            cvPathLabel.setText(currentStudent.getCvFilePath());
        } else {
            cvPathLabel.setText("No CV selected");
        }
    }

    private void chooseCvFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setLocale(Locale.ENGLISH);
        fileChooser.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        fileChooser.setDialogTitle("Select PDF CV");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setApproveButtonText("Open");
        fileChooser.setApproveButtonToolTipText("Open selected PDF");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        UiTheme.normalizeChooserTree(fileChooser);
        SwingUtilities.updateComponentTreeUI(fileChooser);
        int result = fileChooser.showDialog(this, "Open");
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedCvSourceFile = fileChooser.getSelectedFile().toPath();
            cvPathLabel.setText(selectedCvSourceFile.getFileName().toString());
        }
    }

    private void saveProfile() {
        try {
            currentStudent = studentController.saveProfile(
                    currentUser.getId(),
                    nameField.getText().trim(),
                    studentNumberField.getText().trim(),
                    majorField.getText().trim(),
                    gradeField.getText().trim(),
                    parseTags(skillTagsField.getText()),
                    selectedCvSourceFile
            );
            selectedCvSourceFile = null;
            UiTheme.showInfo(this, "Success", "Profile saved successfully.");
            refreshData();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            UiTheme.showError(this, "Profile Save Failed", exception.getMessage());
        }
    }

    private void submitSelectedJob() {
        if (currentStudent == null) {
            UiTheme.showWarning(this, "Profile Required", "Please create your profile before applying.");
            return;
        }

        int selectedRow = jobsTable.getSelectedRow();
        if (selectedRow < 0) {
            UiTheme.showWarning(this, "No Selection", "Please select a job first.");
            return;
        }

        String jobId = jobsModel.getValueAt(selectedRow, 0).toString();
        try {
            applicationController.submitApplication(currentStudent.getId(), jobId);
            UiTheme.showInfo(this, "Success", "Application submitted successfully.");
            refreshData();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            UiTheme.showError(this, "Submission Failed", exception.getMessage());
        }
    }

    private List<String> parseTags(String rawTags) {
        return rawTags == null ? List.of() :
                java.util.Arrays.stream(rawTags.split(","))
                        .map(String::trim)
                        .filter(tag -> !tag.isBlank())
                        .collect(Collectors.toList());
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
