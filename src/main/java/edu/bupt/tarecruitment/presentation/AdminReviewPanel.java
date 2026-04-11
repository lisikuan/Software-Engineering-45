package edu.bupt.tarecruitment.presentation;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.controller.StudentController;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.User;

public class AdminReviewPanel extends JPanel {
    private final User currentUser;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final Runnable logoutAction;
    private final DefaultTableModel applicationsModel;
    private final JTable applicationsTable;
    private final DefaultTableModel jobsModel;
    private final JTable jobsTable;
    private final JTextField courseNameField;
    private final JTextField requiredSkillsField;
    private final JTextField weeklyHoursField;
    private final JTextField quotaField;
    private final JTextArea jobDescriptionArea;
    private final JLabel studentNameLabel;
    private final JLabel studentNumberLabel;
    private final JLabel studentSkillsLabel;
    private final JLabel studentCvLabel;
    private final JTextField skillFilterField;

    private Student selectedStudent;

    public AdminReviewPanel(
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
        this.applicationsModel = new DefaultTableModel(
                new Object[]{"Application ID", "Student ID", "Student Name", "Job ID", "Course", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.applicationsTable = new JTable(applicationsModel);
        this.jobsModel = new DefaultTableModel(
                new Object[]{"Job ID", "Course", "Skills", "Hours", "Quota", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.jobsTable = new JTable(jobsModel);
        this.courseNameField = new JTextField(18);
        this.requiredSkillsField = new JTextField("Java, SQL", 18);
        this.weeklyHoursField = new JTextField(10);
        this.quotaField = new JTextField(6);
        this.jobDescriptionArea = new JTextArea(4, 18);
        this.studentNameLabel = new JLabel("-");
        this.studentNumberLabel = new JLabel("-");
        this.studentSkillsLabel = new JLabel("-");
        this.studentCvLabel = new JLabel("-");
        this.skillFilterField = new JTextField(18);
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(new JLabel("MO Review Dashboard"));
        headerPanel.add(new JLabel("Current User: " + currentUser.getUsername()));
        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildApplicationsPanel(),
                buildRightPanel());
        splitPane.setResizeWeight(0.55);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton approveButton = new JButton("Approve");
        approveButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.APPROVED));
        JButton rejectButton = new JButton("Reject");
        rejectButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.REJECTED));
        JButton reviewedButton = new JButton("Mark Reviewed");
        reviewedButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.REVIEWED));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(event -> logoutAction.run());
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(reviewedButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel buildApplicationsPanel() {
        JPanel applicationsPanel = new JPanel(new BorderLayout(12, 12));
        applicationsPanel.setBorder(BorderFactory.createTitledBorder("Applications"));
        applicationsTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedApplicationDetails();
            }
        });

        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints filterConstraints = new GridBagConstraints();
        filterConstraints.insets = new Insets(4, 4, 4, 4);
        filterConstraints.fill = GridBagConstraints.HORIZONTAL;
        filterConstraints.anchor = GridBagConstraints.WEST;

        JLabel filterLabel = new JLabel("Filter by Skills:");
        JButton filterButton = new JButton("Apply Filter");
        filterButton.addActionListener(event -> refreshDataWithSkillFilter());
        JButton clearFilterButton = new JButton("Clear");
        clearFilterButton.addActionListener(event -> {
            skillFilterField.setText("");
            refreshDataWithSkillFilter();
        });

        filterConstraints.gridx = 0;
        filterConstraints.gridy = 0;
        filterPanel.add(filterLabel, filterConstraints);
        filterConstraints.gridx = 1;
        filterPanel.add(skillFilterField, filterConstraints);
        filterConstraints.gridx = 2;
        filterPanel.add(filterButton, filterConstraints);
        filterConstraints.gridx = 3;
        filterPanel.add(clearFilterButton, filterConstraints);

        applicationsPanel.add(filterPanel, BorderLayout.NORTH);
        applicationsPanel.add(new JScrollPane(applicationsTable), BorderLayout.CENTER);
        return applicationsPanel;
    }

    private JScrollPane buildRightPanel() {
        JPanel rightContent = new JPanel();
        rightContent.setLayout(new BoxLayout(rightContent, BoxLayout.Y_AXIS));

        JPanel jobsSection = buildMyJobsPanel();
        jobsSection.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 150));
        rightContent.add(jobsSection);
        rightContent.add(Box.createVerticalStrut(6));

        rightContent.add(buildJobPublishPanel());
        rightContent.add(Box.createVerticalStrut(6));

        JPanel detailSection = buildStudentDetailPanel();
        detailSection.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 220));
        rightContent.add(detailSection);

        JScrollPane rightScroll = new JScrollPane(rightContent);
        rightScroll.setBorder(BorderFactory.createEmptyBorder());
        rightScroll.getVerticalScrollBar().setUnitIncrement(10);
        return rightScroll;
    }

    private JPanel buildMyJobsPanel() {
        JPanel myJobsPanel = new JPanel(new BorderLayout(6, 6));
        myJobsPanel.setBorder(BorderFactory.createTitledBorder("My Published Jobs"));
        JButton refreshJobsButton = new JButton("Refresh");
        refreshJobsButton.addActionListener(event -> refreshMyJobs());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(refreshJobsButton, BorderLayout.EAST);

        jobsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(0, 100));

        myJobsPanel.add(topBar, BorderLayout.NORTH);
        myJobsPanel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);
        return myJobsPanel;
    }

    private JPanel buildJobPublishPanel() {
        JPanel publishPanel = new JPanel(new GridBagLayout());
        publishPanel.setBorder(BorderFactory.createTitledBorder("Publish Job"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        publishPanel.add(new JLabel("Course Name"), constraints);
        constraints.gridx = 1;
        publishPanel.add(courseNameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        publishPanel.add(new JLabel("Required Skills"), constraints);
        constraints.gridx = 1;
        publishPanel.add(requiredSkillsField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        publishPanel.add(new JLabel("Weekly Hours"), constraints);
        constraints.gridx = 1;
        publishPanel.add(weeklyHoursField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        publishPanel.add(new JLabel("Quota"), constraints);
        constraints.gridx = 1;
        publishPanel.add(quotaField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        publishPanel.add(new JLabel("Description"), constraints);
        constraints.gridx = 1;
        publishPanel.add(new JScrollPane(jobDescriptionArea), constraints);

        JButton publishButton = new JButton("Publish Job");
        publishButton.addActionListener(event -> publishJob());
        constraints.gridx = 1;
        constraints.gridy = 5;
        publishPanel.add(publishButton, constraints);
        return publishPanel;
    }

    private JPanel buildStudentDetailPanel() {
        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Selected Student Profile"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        detailPanel.add(new JLabel("Name"), constraints);
        constraints.gridx = 1;
        detailPanel.add(studentNameLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        detailPanel.add(new JLabel("Student Number"), constraints);
        constraints.gridx = 1;
        detailPanel.add(studentNumberLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        detailPanel.add(new JLabel("Skill Tags"), constraints);
        constraints.gridx = 1;
        detailPanel.add(studentSkillsLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        detailPanel.add(new JLabel("CV"), constraints);
        constraints.gridx = 1;
        detailPanel.add(studentCvLabel, constraints);

        JButton openCvButton = new JButton("Open CV");
        openCvButton.addActionListener(event -> openSelectedStudentCv());
        constraints.gridx = 1;
        constraints.gridy = 4;
        detailPanel.add(openCvButton, constraints);
        return detailPanel;
    }

    private void refreshData() {
        refreshDataWithSkillFilter();
        refreshMyJobs();
    }

    private void refreshDataWithSkillFilter() {
        try {
            applicationsModel.setRowCount(0);
            List<Application> applications;
            String filterText = skillFilterField.getText().trim();
            if (filterText.isEmpty()) {
                applications = applicationController.getApplicationsBySkills(List.of(), currentUser.getId());
            } else {
                applications = applicationController.getApplicationsBySkills(parseTags(filterText), currentUser.getId());
            }
            for (Application application : applications) {
                Student student = studentController.getStudentById(application.getStudentId());
                Job job = jobController.getJobById(application.getJobId());
                applicationsModel.addRow(new Object[]{
                        application.getId(),
                        application.getStudentId(),
                        student.getName(),
                        application.getJobId(),
                        valueOrPlaceholder(job.getCourseName()),
                        application.getStatus().name()
                });
            }
            clearSelectedStudentDetails();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Refresh Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshMyJobs() {
        try {
            jobsModel.setRowCount(0);
            List<Job> myJobs = jobController.getJobsByPublisher(currentUser.getId());
            for (Job job : myJobs) {
                jobsModel.addRow(new Object[]{
                        job.getId(),
                        valueOrPlaceholder(job.getCourseName()),
                        job.getRequiredSkills().isEmpty() ? "-" : String.join(", ", job.getRequiredSkills()),
                        job.getWeeklyHours(),
                        job.getQuota(),
                        job.getStatus().name()
                });
            }
        } catch (DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Load Jobs Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void publishJob() {
        try {
            int weeklyHours = Integer.parseInt(weeklyHoursField.getText().trim());
            int quota = Integer.parseInt(quotaField.getText().trim());
            jobController.publishJob(
                    courseNameField.getText().trim(),
                    parseTags(requiredSkillsField.getText()),
                    weeklyHours,
                    quota,
                    jobDescriptionArea.getText().trim(),
                    currentUser.getId()
            );
            JOptionPane.showMessageDialog(this, "Job published successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            courseNameField.setText("");
            requiredSkillsField.setText("");
            weeklyHoursField.setText("");
            quotaField.setText("");
            jobDescriptionArea.setText("");
            refreshMyJobs();
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Weekly hours must be a valid integer.", "Publish Failed", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Publish Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedApplicationDetails() {
        int selectedRow = applicationsTable.getSelectedRow();
        if (selectedRow < 0) {
            clearSelectedStudentDetails();
            return;
        }

        String studentId = applicationsModel.getValueAt(selectedRow, 1).toString();
        try {
            selectedStudent = studentController.getStudentById(studentId);
            studentNameLabel.setText(valueOrPlaceholder(selectedStudent.getName()));
            studentNumberLabel.setText(valueOrPlaceholder(selectedStudent.getStudentNumber()));
            studentSkillsLabel.setText(selectedStudent.getSkillTags().isEmpty()
                    ? "[???]"
                    : selectedStudent.getSkillTags().stream().collect(Collectors.joining(", ")));
            studentCvLabel.setText(valueOrPlaceholder(selectedStudent.getCvFilePath()));
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Load Detail Failed", JOptionPane.ERROR_MESSAGE);
            clearSelectedStudentDetails();
        }
    }

    private void reviewSelectedApplication(ApplicationStatus newStatus) {
        int selectedRow = applicationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an application first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String applicationId = applicationsModel.getValueAt(selectedRow, 0).toString();
        try {
            applicationController.updateApplicationStatus(applicationId, newStatus);
            JOptionPane.showMessageDialog(this, "Application updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Review Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSelectedStudentCv() {
        if (selectedStudent == null || selectedStudent.getCvFilePath() == null || selectedStudent.getCvFilePath().isBlank()) {
            JOptionPane.showMessageDialog(this, "This student has not uploaded a CV yet.", "No CV", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Path cvPath = studentController.resolveCvFilePath(selectedStudent.getCvFilePath());
            if (!Desktop.isDesktopSupported()) {
                throw new DataAccessException("Desktop file open is not supported in this environment.");
            }
            Desktop.getDesktop().open(cvPath.toFile());
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Open CV Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSelectedStudentDetails() {
        selectedStudent = null;
        studentNameLabel.setText("-");
        studentNumberLabel.setText("-");
        studentSkillsLabel.setText("-");
        studentCvLabel.setText("-");
    }

    private List<String> parseTags(String rawTags) {
        return rawTags == null ? List.of() :
                java.util.Arrays.stream(rawTags.split(","))
                        .map(String::trim)
                        .filter(tag -> !tag.isBlank())
                        .collect(Collectors.toList());
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "[???]" : value;
    }
}
