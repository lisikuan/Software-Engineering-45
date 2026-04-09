package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.model.Application;
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

public class StudentDashboardPanel extends JPanel {
    private final User currentUser;
    private final Student currentStudent;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final Runnable logoutAction;
    private final DefaultTableModel jobsModel;
    private final DefaultTableModel applicationsModel;
    private final JTable jobsTable;

    public StudentDashboardPanel(
            User currentUser,
            Student currentStudent,
            JobController jobController,
            ApplicationController applicationController,
            Runnable logoutAction
    ) {
        this.currentUser = currentUser;
        this.currentStudent = currentStudent;
        this.jobController = jobController;
        this.applicationController = applicationController;
        this.logoutAction = logoutAction;
        this.jobsModel = new DefaultTableModel(new Object[]{"Job ID", "Title", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.applicationsModel = new DefaultTableModel(new Object[]{"Application ID", "Job ID", "Job Title", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.jobsTable = new JTable(jobsModel);
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(new JLabel("Student Dashboard"));
        headerPanel.add(new JLabel("Current User: " + currentUser.getUsername() + " / Student: " + currentStudent.getName()));
        headerPanel.add(new JLabel("[待确认] Student profile fields remain minimal in the test version."));
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);

        JTable applicationsTable = new JTable(applicationsModel);
        centerPanel.add(new JScrollPane(applicationsTable), BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

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

    private void refreshData() {
        try {
            List<Job> jobs = jobController.getAllJobs();
            Map<String, String> jobTitles = new HashMap<>();
            jobsModel.setRowCount(0);
            for (Job job : jobs) {
                jobsModel.addRow(new Object[]{job.getId(), job.getTitle(), job.getDescription()});
                jobTitles.put(job.getId(), job.getTitle());
            }

            applicationsModel.setRowCount(0);
            List<Application> applications = applicationController.getApplicationsForStudent(currentStudent.getId());
            for (Application application : applications) {
                applicationsModel.addRow(new Object[]{
                        application.getId(),
                        application.getJobId(),
                        jobTitles.getOrDefault(application.getJobId(), "Unknown Job"),
                        application.getStatus().name()
                });
            }
        } catch (ValidationException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Refresh Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitSelectedJob() {
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
}
