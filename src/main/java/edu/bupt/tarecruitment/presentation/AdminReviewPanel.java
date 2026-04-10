package edu.bupt.tarecruitment.presentation;

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

import javax.swing.BorderFactory;
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
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class AdminReviewPanel extends JPanel {
    private final User currentUser;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final Runnable logoutAction;
    private final DefaultTableModel applicationsModel;
    private final JTable applicationsTable;
    private final JTextField courseNameField;
    private final JTextField requiredSkillsField;
    private final JTextField weeklyHoursField;
    private final JTextArea jobDescriptionArea;
    private final JLabel studentNameLabel;
    private final JLabel studentNumberLabel;
    private final JLabel studentSkillsLabel;
    private final JLabel studentCvLabel;

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
        this.courseNameField = new JTextField(18);
        this.requiredSkillsField = new JTextField(18);
        this.weeklyHoursField = new JTextField(10);
        this.jobDescriptionArea = new JTextArea(4, 18);
        this.studentNameLabel = new JLabel("-");
        this.studentNumberLabel = new JLabel("-");
        this.studentSkillsLabel = new JLabel("-");
        this.studentCvLabel = new JLabel("-");
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(new JLabel("Admin Review Dashboard"));
        headerPanel.add(new JLabel("Current User: " + currentUser.getUsername()));
        headerPanel.add(new JLabel("[待确认] MO is merged into ADMIN for the current runnable test version."));
        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildApplicationsPanel(), buildRightPanel());
        splitPane.setResizeWeight(0.55);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton approveButton = new JButton("Approve");
        approveButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.APPROVED));
        JButton rejectButton = new JButton("Reject");
        rejectButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.REJECTED));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(event -> logoutAction.run());
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
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
        applicationsPanel.add(new JScrollPane(applicationsTable), BorderLayout.CENTER);
        return applicationsPanel;
    }

    private JPanel buildRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(12, 12));
        rightPanel.add(buildJobPublishPanel(), BorderLayout.NORTH);
        rightPanel.add(buildStudentDetailPanel(), BorderLayout.CENTER);
        return rightPanel;
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
        publishPanel.add(new JLabel("Description"), constraints);
        constraints.gridx = 1;
        publishPanel.add(new JScrollPane(jobDescriptionArea), constraints);

        JButton publishButton = new JButton("Publish Job");
        publishButton.addActionListener(event -> publishJob());
        constraints.gridx = 1;
        constraints.gridy = 4;
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
        try {
            applicationsModel.setRowCount(0);
            List<Application> applications = applicationController.getAllApplications();
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

    private void publishJob() {
        try {
            int weeklyHours = Integer.parseInt(weeklyHoursField.getText().trim());
            jobController.publishJob(
                    courseNameField.getText().trim(),
                    parseTags(requiredSkillsField.getText()),
                    weeklyHours,
                    jobDescriptionArea.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Job published successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            courseNameField.setText("");
            requiredSkillsField.setText("");
            weeklyHoursField.setText("");
            jobDescriptionArea.setText("");
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
                    ? "[待确认]"
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
        return value == null || value.isBlank() ? "[待确认]" : value;
    }
}
