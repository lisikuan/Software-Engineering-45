package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.AuthController;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {
    private final AuthController authController;
    private final Consumer<User> loginSuccessHandler;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<UserRole> roleBox;

    public LoginPanel(AuthController authController, Consumer<User> loginSuccessHandler) {
        this.authController = authController;
        this.loginSuccessHandler = loginSuccessHandler;
        this.usernameField = new JTextField(18);
        this.passwordField = new JPasswordField(18);
        this.roleBox = new JComboBox<>(new UserRole[]{UserRole.TA, UserRole.MO});
        initializeUi();
    }

    private void initializeUi() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(new JLabel("TA Recruitment System Login"), constraints);

        constraints.gridwidth = 1;
        constraints.gridy = 1;
        add(new JLabel("Username"), constraints);
        constraints.gridx = 1;
        add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(new JLabel("Password"), constraints);
        constraints.gridx = 1;
        add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(new JLabel("Role"), constraints);
        constraints.gridx = 1;
        add(roleBox, constraints);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> handleLogin());
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        add(loginButton, constraints);
    }

    private void handleLogin() {
        try {
            User user = authController.login(
                    usernameField.getText().trim(),
                    new String(passwordField.getPassword()),
                    (UserRole) roleBox.getSelectedItem()
            );
            loginSuccessHandler.accept(user);
        } catch (ValidationException | BusinessException | DataAccessException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
