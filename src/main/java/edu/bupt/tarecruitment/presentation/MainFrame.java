package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.ApplicationController;
import edu.bupt.tarecruitment.controller.AuthController;
import edu.bupt.tarecruitment.controller.JobController;
import edu.bupt.tarecruitment.controller.StudentController;
import edu.bupt.tarecruitment.model.Student;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
        setSize(1000, 700);
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
        try {
            if (user.getRole() == UserRole.STUDENT) {
                Student student = studentController.getStudentByUserId(user.getId());
                contentPanel.add(
                        new StudentDashboardPanel(
                                user,
                                student,
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
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            showLoginView();
        }
    }

    private void refreshFrame() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
