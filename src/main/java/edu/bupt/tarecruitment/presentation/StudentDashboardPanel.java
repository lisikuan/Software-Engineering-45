package edu.bupt.tarecruitment.presentation;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

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

public class StudentDashboardPanel extends JPanel {
    private final User currentUser;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final Runnable logoutAction;
    private final DefaultTableModel jobsModel;
    private final DefaultTableModel applicationsModel;
    private final JTable jobsTable;
    private final JLabel profileStatusLabel;
    private final JTextField nameField;
    private final JTextField studentNumberField;
    private final JTextField majorField;
    private final JTextField gradeField;
    private final JTextField skillTagsField;
    private final JLabel cvPathLabel;

    private Student currentStudent;
    private Path selectedCvSourceFile;

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
        this.profileStatusLabel = new JLabel();
        this.nameField = new JTextField(18);
        this.studentNumberField = new JTextField(18);
        this.majorField = new JTextField(18);
        this.gradeField = new JTextField(18);
        this.skillTagsField = new JTextField(18);
        this.cvPathLabel = new JLabel("No CV selected");
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(new JLabel("TA Dashboard"));
        headerPanel.add(new JLabel("Current User: " + currentUser.getUsername()));
        headerPanel.add(profileStatusLabel);
        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildProfilePanel(), buildJobsPanel());
        splitPane.setResizeWeight(0.35);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit Selected Job");
        submitButton.addActionListener(event -> submitSelectedJob());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(event -> logoutAction.run());
        buttonPanel.add(submitButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel buildProfilePanel() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("Student Profile"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        profilePanel.add(new JLabel("Name"), constraints);
        constraints.gridx = 1;
        profilePanel.add(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        profilePanel.add(new JLabel("Student Number"), constraints);
        constraints.gridx = 1;
        profilePanel.add(studentNumberField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        profilePanel.add(new JLabel("Major"), constraints);
        constraints.gridx = 1;
        profilePanel.add(majorField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        profilePanel.add(new JLabel("Grade"), constraints);
        constraints.gridx = 1;
        profilePanel.add(gradeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        profilePanel.add(new JLabel("Skill Tags"), constraints);
        constraints.gridx = 1;
        profilePanel.add(skillTagsField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        profilePanel.add(new JLabel("CV"), constraints);
        constraints.gridx = 1;
        profilePanel.add(cvPathLabel, constraints);

        JButton chooseCvButton = new JButton("Choose PDF CV");
        chooseCvButton.addActionListener(event -> chooseCvFile());
        constraints.gridx = 1;
        constraints.gridy = 6;
        profilePanel.add(chooseCvButton, constraints);

        JButton saveProfileButton = new JButton("Create / Update Profile");
        saveProfileButton.addActionListener(event -> saveProfile());
        constraints.gridx = 1;
        constraints.gridy = 7;
        profilePanel.add(saveProfileButton, constraints);

        return profilePanel;
    }

    private JPanel buildJobsPanel() {
        JPanel jobsPanel = new JPanel(new BorderLayout(12, 12));
        jobsPanel.setBorder(BorderFactory.createTitledBorder("Jobs and Applications"));

        jobsPanel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);

        JTable applicationsTable = new JTable(applicationsModel);
        JScrollPane applicationsScrollPane = new JScrollPane(applicationsTable);
        applicationsScrollPane.setBorder(BorderFactory.createTitledBorder("My Applications"));
        jobsPanel.add(applicationsScrollPane, BorderLayout.SOUTH);
        return jobsPanel;
    }

    private void refreshData() {
        try {
            currentStudent = studentController.findStudentByUserId(currentUser.getId()).orElse(null);
            updateProfileForm();

            List<Job> jobs = jobController.getOpenJobs();
            Map<String, String> jobCourses = new HashMap<>();
            jobsModel.setRowCount(0);
            for (Job job : jobs) {
                String missingSkills = currentStudent == null ? "-" : String.join(", ", studentController.getMissingSkills(currentStudent.getId(), job));
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
                for (Application application : applications) {
                    applicationsModel.addRow(new Object[]{
                            application.getId(),
                            application.getJobId(),
                            jobCourses.getOrDefault(application.getJobId(), "Unknown Course"),
                            application.getStatus().name()
                    });
                }
            }
        } catch (ValidationException | DataAccessException | BusinessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Refresh Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProfileForm() {
        if (currentStudent == null) {
            profileStatusLabel.setText("Profile status: not created yet.");
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
        fileChooser.setDialogTitle("Select PDF CV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        int result = fileChooser.showOpenDialog(this);
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
            JOptionPane.showMessageDialog(this, "Profile saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Profile Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitSelectedJob() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Please create your profile before applying.", "Profile Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = jobsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a job first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String jobId = jobsModel.getValueAt(selectedRow, 0).toString();
        try {
            applicationController.submitApplication(currentStudent.getId(), jobId);
            JOptionPane.showMessageDialog(this, "Application submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Submission Failed", JOptionPane.ERROR_MESSAGE);
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
        return value == null || value.isBlank() ? "[待确认]" : value;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
