package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.controller.AiController;
import edu.bupt.tarecruitment.controller.AdminController;
import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.AuthController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.controller.StudentController;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class MainFrame extends JFrame {
    private static final String LOGIN_VIEW = "login";
    private static final String TA_VIEW = "ta";
    private static final String MO_VIEW = "mo";
    private static final String ADMIN_VIEW = "admin";

    private final AuthController authController;
    private final AdminController adminController;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final AiController aiController;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public MainFrame(
            AuthController authController,
            StudentController studentController,
            JobController jobController,
            ApplicationController applicationController
    ) {
        this(authController, null, studentController, jobController, applicationController, null);
    }

    public MainFrame(
            AuthController authController,
            AdminController adminController,
            StudentController studentController,
            JobController jobController,
            ApplicationController applicationController,
            AiController aiController
    ) {
        super("TA Recruitment System");
        this.authController = authController;
        this.adminController = adminController;
        this.studentController = studentController;
        this.jobController = jobController;
        this.applicationController = applicationController;
        this.aiController = aiController;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        initializeUi();
    }

    private void initializeUi() {
        UiTheme.installDefaults();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setLocationRelativeTo(null);
        contentPanel.setBackground(UiTheme.PAGE_BG);
        setContentPane(contentPanel);
        showLoginView();
    }

    private void showLoginView() {
        contentPanel.removeAll();
        contentPanel.add(new LoginPanel(authController, this::handleLoginSuccess), LOGIN_VIEW);
        cardLayout.show(contentPanel, LOGIN_VIEW);
        refreshFrame();
    }

    private void handleLoginSuccess(User user) {
        if (user.getRole() == UserRole.TA) {
            contentPanel.add(
                    new StudentDashboardPanel(
                            user,
                            studentController,
                            jobController,
                            applicationController,
                            this::showLoginView
                    ),
                    TA_VIEW
            );
            cardLayout.show(contentPanel, TA_VIEW);
        } else if (user.getRole() == UserRole.MO) {
            contentPanel.add(
                    new AdminReviewPanel(
                            user,
                            studentController,
                            jobController,
                            applicationController,
                            aiController,
                            this::showLoginView
                    ),
                    MO_VIEW
            );
            cardLayout.show(contentPanel, MO_VIEW);
        } else {
            contentPanel.add(
                    new AdminDashboardPanel(
                            user,
                            adminController,
                            jobController,
                            applicationController,
                            aiController,
                            this::showLoginView
                    ),
                    ADMIN_VIEW
            );
            cardLayout.show(contentPanel, ADMIN_VIEW);
        }
        refreshFrame();
    }

    private void refreshFrame() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
