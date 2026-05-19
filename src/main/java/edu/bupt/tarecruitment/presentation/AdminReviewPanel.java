package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.AiController;
import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.controller.StudentController;
import edu.bupt.tarecruitment.model.Application;
import edu.bupt.tarecruitment.model.ApplicationStatus;
import edu.bupt.tarecruitment.model.Job;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.WorkloadReport;
import edu.bupt.tarecruitment.model.User;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class AdminReviewPanel extends JPanel {
    private static final String VIEW_APPLICATIONS = "applications";
    private static final String VIEW_MODULES = "modules";
    private static final String VIEW_WORKLOAD = "workload";

    private final User currentUser;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final AiController aiController;
    private final Runnable logoutAction;

    private final DefaultTableModel applicationsModel;
    private final JTable applicationsTable;
    private final DefaultTableModel jobsModel;
    private final JTable jobsTable;
    private final DefaultTableModel workloadModel;
    private final JTable workloadTable;

    private final JTextField courseNameField;
    private final JTextField requiredSkillsField;
    private final JTextField weeklyHoursField;
    private final JTextField quotaField;
    private final JTextArea jobDescriptionArea;
    private final JTextField skillFilterField;

    private final JLabel studentNameLabel;
    private final JLabel studentNumberLabel;
    private final JLabel studentCvFileLabel;
    private final JPanel studentSkillTagsPanel;

    private final DashboardShell.StatCard totalApplicationsCard;
    private final DashboardShell.StatCard pendingReviewCard;
    private final DashboardShell.StatCard activeModulesCard;
    private final DashboardShell.StatCard overloadedCard;

    private Student selectedStudent;
    private String currentView;

    public AdminReviewPanel(
            User currentUser,
            StudentController studentController,
            JobController jobController,
            ApplicationController applicationController,
            Runnable logoutAction
    ) {
        this(currentUser, studentController, jobController, applicationController, null, logoutAction);
    }

    public AdminReviewPanel(
            User currentUser,
            StudentController studentController,
            JobController jobController,
            ApplicationController applicationController,
            AiController aiController,
            Runnable logoutAction
    ) {
        this.currentUser = currentUser;
        this.studentController = studentController;
        this.jobController = jobController;
        this.applicationController = applicationController;
        this.aiController = aiController;
        this.logoutAction = logoutAction;
        this.applicationsModel = new DefaultTableModel(
                new Object[]{"Application ID", "Student ID", "Student Name", "Course", "Skills", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.applicationsTable = new JTable(applicationsModel);
        this.jobsModel = new DefaultTableModel(
                new Object[]{"Job ID", "Module", "Skills", "Hours", "Quota", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.jobsTable = new JTable(jobsModel);
        this.workloadModel = new DefaultTableModel(
                new Object[]{"TA ID", "Student", "Approved Jobs", "Weekly Hours", "Risk"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.workloadTable = new JTable(workloadModel);
        this.courseNameField = new JTextField(18);
        this.requiredSkillsField = new JTextField(18);
        this.weeklyHoursField = new JTextField(18);
        this.quotaField = new JTextField(18);
        this.jobDescriptionArea = new JTextArea(5, 18);
        this.skillFilterField = new JTextField(18);
        this.studentNameLabel = new JLabel("-");
        this.studentNumberLabel = new JLabel("-");
        this.studentCvFileLabel = new JLabel("-");
        this.studentSkillTagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        this.totalApplicationsCard = DashboardShell.statCard("Total Applications", "0");
        this.pendingReviewCard = DashboardShell.statCard("Pending Review", "0");
        this.activeModulesCard = DashboardShell.statCard("Active Modules", "0");
        this.overloadedCard = DashboardShell.statCard("Overloaded", "0");
        this.currentView = VIEW_APPLICATIONS;
        initializeUi();
        refreshData();
    }

    private void initializeUi() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.PAGE_BG);

        UiTheme.styleTable(applicationsTable);
        UiTheme.styleTable(jobsTable);
        UiTheme.styleTable(workloadTable);
        UiTheme.styleField(courseNameField);
        UiTheme.styleField(requiredSkillsField);
        UiTheme.styleField(weeklyHoursField);
        UiTheme.styleField(quotaField);
        UiTheme.styleField(skillFilterField);
        UiTheme.styleTextArea(jobDescriptionArea);
        studentSkillTagsPanel.setOpaque(false);
        UiTheme.styleFlatLabel(studentCvFileLabel);
        applicationsTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedApplicationDetails();
            }
        });
        renderView();
    }

    private void renderView() {
        removeAll();

        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setOpaque(false);
        content.add(
                DashboardShell.buildStatsRow(
                        totalApplicationsCard,
                        pendingReviewCard,
                        activeModulesCard,
                        overloadedCard
                ),
                BorderLayout.NORTH
        );
        content.add(buildBodyForCurrentView(), BorderLayout.CENTER);

        add(DashboardShell.buildShell(
                "MO Portal",
                List.of(
                        new DashboardShell.NavItem("01", "Applications", VIEW_APPLICATIONS.equals(currentView), () -> switchView(VIEW_APPLICATIONS)),
                        new DashboardShell.NavItem("02", "Modules", VIEW_MODULES.equals(currentView), () -> switchView(VIEW_MODULES)),
                        new DashboardShell.NavItem("03", "Workload", VIEW_WORKLOAD.equals(currentView), () -> switchView(VIEW_WORKLOAD)),
                        new DashboardShell.NavItem("04", "Logout", false, logoutAction)
                ),
                currentUser.getUsername(),
                "Module Organiser",
                viewTitle(),
                viewSubtitle(),
                content
        ), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel buildBodyForCurrentView() {
        return switch (currentView) {
            case VIEW_MODULES -> buildModulesView();
            case VIEW_WORKLOAD -> buildWorkloadView();
            default -> buildApplicationsView();
        };
    }

    private JPanel buildApplicationsView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);

        JSplitPane upper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildApplicationsPanel(), buildStudentDetailPanel());
        upper.setResizeWeight(0.72);
        UiTheme.styleSplitPane(upper);

        body.add(upper, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildModulesView() {
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        JPanel modules = new JPanel(new GridLayout(1, 2, 18, 0));
        modules.setOpaque(false);
        modules.add(buildJobPublishPanel());
        modules.add(buildPublishedJobsPanel());
        body.add(modules, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildWorkloadView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(buildWorkloadPanel(), BorderLayout.CENTER);
        return body;
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
            case VIEW_MODULES -> "Modules & Positions";
            case VIEW_WORKLOAD -> "Workload Monitor";
            default -> "Application Management";
        };
    }

    private String viewSubtitle() {
        return switch (currentView) {
            case VIEW_MODULES -> "Publish roles, review module demand, and track open positions.";
            case VIEW_WORKLOAD -> "Monitor assigned hours and rebalance staffing before approval.";
            default -> "Review applicants, publish positions, and monitor staffing load.";
        };
    }

    private JPanel buildApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        UiTheme.styleSection(panel);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(UiTheme.sectionTitle("Application Management"), BorderLayout.WEST);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titleRow);
        header.add(Box.createVerticalStrut(12));
        header.add(buildReviewActions());

        JPanel filters = new JPanel(new BorderLayout(12, 0));
        filters.setOpaque(false);
        filters.add(skillFilterField, BorderLayout.CENTER);

        JPanel filterButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterButtons.setOpaque(false);
        JButton filterButton = new JButton("Apply Filter");
        filterButton.addActionListener(event -> refreshDataWithSkillFilter());
        UiTheme.styleSecondaryButton(filterButton);
        JButton clearFilterButton = new JButton("Clear");
        clearFilterButton.addActionListener(event -> {
            skillFilterField.setText("");
            refreshDataWithSkillFilter();
        });
        UiTheme.styleSecondaryButton(clearFilterButton);
        filterButtons.add(filterButton);
        filterButtons.add(clearFilterButton);
        filters.add(filterButtons, BorderLayout.EAST);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(header);
        top.add(Box.createVerticalStrut(12));
        top.add(filters);

        panel.add(top, BorderLayout.NORTH);
        panel.add(UiTheme.styleScrollPane(new JScrollPane(applicationsTable)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReviewActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton approveButton = new JButton("Approve");
        approveButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.APPROVED));
        UiTheme.stylePrimaryButton(approveButton);

        JButton rejectButton = new JButton("Reject");
        rejectButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.REJECTED));
        UiTheme.styleDangerButton(rejectButton);

        JButton reviewedButton = new JButton("Mark Reviewed");
        reviewedButton.addActionListener(event -> reviewSelectedApplication(ApplicationStatus.REVIEWED));
        UiTheme.styleSecondaryButton(reviewedButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshData());
        UiTheme.styleSecondaryButton(refreshButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(event -> logoutAction.run());
        UiTheme.styleDangerButton(logoutButton);

        actions.add(approveButton);
        actions.add(rejectButton);
        actions.add(reviewedButton);
        actions.add(refreshButton);
        actions.add(logoutButton);
        return actions;
    }

    private JScrollPane buildRightRail() {
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.add(buildJobPublishPanel());
        column.add(Box.createVerticalStrut(16));
        column.add(buildPublishedJobsPanel());
        column.add(Box.createVerticalStrut(16));
        column.add(buildStudentDetailPanel());

        JScrollPane scrollPane = new JScrollPane(column);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UiTheme.PAGE_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        return scrollPane;
    }

    private JPanel buildJobPublishPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        UiTheme.styleSection(panel);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.sectionTitle("Publish Position"), BorderLayout.WEST);
        header.add(UiTheme.badge("MO"), BorderLayout.EAST);
        panel.add(header, c);

        c.gridwidth = 1;
        c.gridy++;
        panel.add(fieldLabel("Module Name"), c);
        c.gridx = 1;
        panel.add(courseNameField, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(fieldLabel("Required Skills"), c);
        c.gridx = 1;
        panel.add(requiredSkillsField, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(fieldLabel("Weekly Hours"), c);
        c.gridx = 1;
        panel.add(weeklyHoursField, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(fieldLabel("Quota"), c);
        c.gridx = 1;
        panel.add(quotaField, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(fieldLabel("Description"), c);
        c.gridx = 1;
        panel.add(UiTheme.styleScrollPane(new JScrollPane(jobDescriptionArea)), c);

        c.gridx = 1;
        c.gridy++;
        JButton publishButton = new JButton("Post Job");
        publishButton.addActionListener(event -> publishJob());
        UiTheme.stylePrimaryButton(publishButton);
        panel.add(publishButton, c);

        return panel;
    }

    private JPanel buildPublishedJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        UiTheme.styleSection(panel);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.sectionTitle("Published Jobs"), BorderLayout.WEST);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshMyJobs());
        UiTheme.styleSecondaryButton(refreshButton);
        header.add(refreshButton, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(buildTableModule("Published Positions", "Current modules and staffing states", jobsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStudentDetailPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        UiTheme.styleSection(card);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.sectionTitle("Selected Student Profile"), BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        UiTheme.styleFlatLabel(studentNameLabel);
        UiTheme.styleFlatLabel(studentNumberLabel);

        content.add(detailBlock("Name", buildValuePanel(studentNameLabel, 38)));
        content.add(Box.createVerticalStrut(8));
        content.add(detailBlock("Student Number", buildValuePanel(studentNumberLabel, 38)));
        content.add(Box.createVerticalStrut(8));

        content.add(detailBlock("Skill Tags", buildTagSummaryPanel()));
        content.add(Box.createVerticalStrut(8));

        content.add(detailBlock("CV File", buildValuePanel(studentCvFileLabel, 54)));
        content.add(Box.createVerticalStrut(8));

        JLabel hint = UiTheme.mutedLabel("Open the submitted CV in your PDF viewer.");
        hint.setAlignmentX(LEFT_ALIGNMENT);
        content.add(hint);
        content.add(Box.createVerticalStrut(10));

        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 10));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        JButton openCvButton = new JButton("Open CV");
        openCvButton.addActionListener(event -> openSelectedStudentCv());
        UiTheme.styleSecondaryButton(openCvButton);
        JButton clearButton = new JButton("Clear Selection");
        clearButton.addActionListener(event -> {
            applicationsTable.clearSelection();
            clearSelectedStudentDetails();
        });
        UiTheme.styleSecondaryButton(clearButton);
        actions.add(openCvButton);
        actions.add(clearButton);
        content.add(actions);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UiTheme.SURFACE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildWorkloadPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        UiTheme.styleSection(panel);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.sectionTitle("Workload Monitor"), BorderLayout.WEST);
        JLabel hint = UiTheme.mutedLabel("Compact staffing overview");
        header.add(hint, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);
        panel.add(buildTableModule("TA Workload", "Use this overview before rebalancing staffing", workloadTable), BorderLayout.CENTER);
        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    private JPanel detailBlock(String title, java.awt.Component component) {
        JPanel block = new JPanel(new BorderLayout(0, 8));
        block.setOpaque(false);
        block.setAlignmentX(LEFT_ALIGNMENT);
        block.add(fieldLabel(title), BorderLayout.NORTH);
        block.add(component, BorderLayout.CENTER);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 28));
        return block;
    }

    private JPanel buildValuePanel(JLabel label, int height) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(UiTheme.SURFACE_ALT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.LINE_STRONG, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        label.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(220, height));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return panel;
    }

    private JPanel buildTagSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(UiTheme.SURFACE_ALT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.LINE_STRONG, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        panel.add(studentSkillTagsPanel, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(220, 72));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        return panel;
    }

    private JLabel buildTagChip(String text) {
        JLabel chip = new JLabel(text);
        chip.setOpaque(true);
        chip.setBackground(UiTheme.BRAND_SOFT);
        chip.setForeground(UiTheme.BRAND);
        chip.setFont(UiTheme.SMALL_FONT);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return chip;
    }

    private JPanel buildTableModule(String title, String subtitle, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(UiTheme.sectionTitle(title));
        top.add(Box.createVerticalStrut(4));
        top.add(UiTheme.mutedLabel(subtitle));

        panel.add(top, BorderLayout.NORTH);
        panel.add(UiTheme.styleScrollPane(new JScrollPane(table)), BorderLayout.CENTER);
        return panel;
    }

    private void refreshData() {
        refreshDataWithSkillFilter();
        refreshMyJobs();
        refreshWorkload();
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

            int pendingCount = 0;
            for (Application application : applications) {
                Student student = studentController.getStudentById(application.getStudentId());
                Job job = jobController.getJobById(application.getJobId());
                applicationsModel.addRow(new Object[]{
                        application.getId(),
                        application.getStudentId(),
                        student.getName(),
                        valueOrPlaceholder(job.getCourseName()),
                        student.getSkillTags().isEmpty() ? "-" : String.join(", ", student.getSkillTags()),
                        application.getStatus().name()
                });
                if (application.getStatus() == ApplicationStatus.SUBMITTED || application.getStatus() == ApplicationStatus.REVIEWED) {
                    pendingCount++;
                }
            }

            totalApplicationsCard.setValue(String.valueOf(applications.size()));
            pendingReviewCard.setValue(String.valueOf(pendingCount));
            clearSelectedStudentDetails();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            UiTheme.showError(this, "Refresh Failed", exception.getMessage());
        }
    }

    private void refreshMyJobs() {
        try {
            jobsModel.setRowCount(0);
            List<Job> myJobs = jobController.getJobsByPublisher(currentUser.getId());
            int openJobs = 0;
            for (Job job : myJobs) {
                jobsModel.addRow(new Object[]{
                        job.getId(),
                        valueOrPlaceholder(job.getCourseName()),
                        job.getRequiredSkills().isEmpty() ? "-" : String.join(", ", job.getRequiredSkills()),
                        job.getWeeklyHours(),
                        job.getQuota(),
                        job.getStatus().name()
                });
                if ("OPEN".equalsIgnoreCase(job.getStatus().name())) {
                    openJobs++;
                }
            }
            activeModulesCard.setValue(String.valueOf(openJobs));
        } catch (DataAccessException exception) {
            UiTheme.showError(this, "Load Jobs Failed", exception.getMessage());
        }
    }

    private void refreshWorkload() {
        workloadModel.setRowCount(0);
        if (aiController == null) {
            overloadedCard.setValue("-");
            workloadModel.addRow(new Object[]{"-", "Workload service unavailable", "-", "-", "-"});
            return;
        }

        try {
            List<WorkloadReport> reports = aiController.getWorkloadReport();
            int overloadedCount = 0;
            for (WorkloadReport report : reports) {
                String risk = report.isOverloaded() ? "Overloaded"
                        : report.getTotalWeeklyHours() >= 16 ? "Warning"
                        : "Normal";
                if (report.isOverloaded()) {
                    overloadedCount++;
                }
                workloadModel.addRow(new Object[]{
                        report.getStudentId(),
                        report.getStudentName(),
                        report.getApprovedJobCount(),
                        report.getTotalWeeklyHours(),
                        risk
                });
            }
            overloadedCard.setValue(String.valueOf(overloadedCount));
        } catch (DataAccessException exception) {
            overloadedCard.setValue("!");
            UiTheme.showError(this, "Workload Failed", exception.getMessage());
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
            UiTheme.showInfo(this, "Success", "Job published successfully.");
            courseNameField.setText("");
            requiredSkillsField.setText("");
            weeklyHoursField.setText("");
            quotaField.setText("");
            jobDescriptionArea.setText("");
            refreshMyJobs();
        } catch (NumberFormatException exception) {
            UiTheme.showError(this, "Publish Failed", "Weekly hours and quota must be valid integers.");
        } catch (ValidationException | DataAccessException exception) {
            UiTheme.showError(this, "Publish Failed", exception.getMessage());
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
            studentSkillTagsPanel.removeAll();
            if (selectedStudent.getSkillTags().isEmpty()) {
                studentSkillTagsPanel.add(UiTheme.mutedLabel("-"));
            } else {
                for (String tag : selectedStudent.getSkillTags()) {
                    studentSkillTagsPanel.add(buildTagChip(tag));
                }
            }
            studentCvFileLabel.setText(valueOrPlaceholder(selectedStudent.getCvFilePath()));
            studentSkillTagsPanel.revalidate();
            studentSkillTagsPanel.repaint();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            UiTheme.showError(this, "Load Detail Failed", exception.getMessage());
            clearSelectedStudentDetails();
        }
    }

    private void reviewSelectedApplication(ApplicationStatus newStatus) {
        int selectedRow = applicationsTable.getSelectedRow();
        if (selectedRow < 0) {
            UiTheme.showWarning(this, "No Selection", "Please select an application first.");
            return;
        }

        String applicationId = applicationsModel.getValueAt(selectedRow, 0).toString();
        try {
            applicationController.updateApplicationStatus(applicationId, newStatus);
            UiTheme.showInfo(this, "Success", "Application updated successfully.");
            refreshData();
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            UiTheme.showError(this, "Review Failed", exception.getMessage());
        }
    }

    private void openSelectedStudentCv() {
        if (selectedStudent == null || selectedStudent.getCvFilePath() == null || selectedStudent.getCvFilePath().isBlank()) {
            UiTheme.showWarning(this, "No CV", "This student has not uploaded a CV yet.");
            return;
        }

        try {
            Path cvPath = studentController.resolveCvFilePath(selectedStudent.getCvFilePath());
            if (!Desktop.isDesktopSupported()) {
                throw new DataAccessException("Desktop file open is not supported in this environment.");
            }
            Desktop.getDesktop().open(cvPath.toFile());
        } catch (Exception exception) {
            UiTheme.showError(this, "Open CV Failed", exception.getMessage());
        }
    }

    private void clearSelectedStudentDetails() {
        selectedStudent = null;
        studentNameLabel.setText("-");
        studentNumberLabel.setText("-");
        studentCvFileLabel.setText("-");
        studentSkillTagsPanel.removeAll();
        studentSkillTagsPanel.add(UiTheme.mutedLabel("-"));
        studentSkillTagsPanel.revalidate();
        studentSkillTagsPanel.repaint();
    }

    private List<String> parseTags(String rawTags) {
        return rawTags == null ? List.of()
                : java.util.Arrays.stream(rawTags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .collect(Collectors.toList());
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
