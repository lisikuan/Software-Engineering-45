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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminReviewPanel extends JPanel {
    private final User currentUser;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final Runnable logoutAction;
    private final DefaultTableModel applicationsModel;
    private final JTable applicationsTable;

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
                new Object[]{"Application ID", "Student ID", "Student Name", "Job ID", "Job Title", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.applicationsTable = new JTable(applicationsModel);
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

        add(new JScrollPane(applicationsTable), BorderLayout.CENTER);

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

    private void refreshData() {
        try {
            Map<String, String> jobTitles = new HashMap<>();
            for (Job job : jobController.getAllJobs()) {
                jobTitles.put(job.getId(), job.getTitle());
            }

            Map<String, String> studentNames = new HashMap<>();
            for (Student student : studentController.getAllStudents()) {
                studentNames.put(student.getId(), student.getName());
            }

            applicationsModel.setRowCount(0);
            List<Application> applications = applicationController.getAllApplications();
            for (Application application : applications) {
                applicationsModel.addRow(new Object[]{
                        application.getId(),
                        application.getStudentId(),
                        studentNames.getOrDefault(application.getStudentId(), "Unknown Student"),
                        application.getJobId(),
                        jobTitles.getOrDefault(application.getJobId(), "Unknown Job"),
                        application.getStatus().name()
                });
            }
        } catch (DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Refresh Failed", JOptionPane.ERROR_MESSAGE);
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
}
