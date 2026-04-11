package edu.bupt.tarecruitment.presentation;

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
    private static final String STUDENT_VIEW = "student";
    private static final String ADMIN_VIEW = "admin";

    private final AuthController authController;
    private final StudentController studentController;
    private final JobController jobController;
    private final ApplicationController applicationController;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public MainFrame(
            AuthController authController,
            StudentController studentController,
            JobController jobController,
            ApplicationController applicationController
    ) {
        super("TA Recruitment System");
        this.authController = authController;
        this.studentController = studentController;
        this.jobController = jobController;
        this.applicationController = applicationController;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        initializeUi();
    }

    private void initializeUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setLocationRelativeTo(null);
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
        if (user.getRole() == UserRole.STUDENT) {
            contentPanel.add(
                    new StudentDashboardPanel(
                            user,
                            studentController,
                            jobController,
                            applicationController,
                            this::showLoginView
                    ),
                    STUDENT_VIEW
            );
            cardLayout.show(contentPanel, STUDENT_VIEW);
        } else {
            contentPanel.add(
                    new AdminReviewPanel(
                            user,
                            studentController,
                            jobController,
                            applicationController,
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
