package edu.bupt.tarecruitment.presentation;

import edu.bupt.tarecruitment.common.exception.BusinessException;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.ValidationException;
import edu.bupt.tarecruitment.controller.AuthController;
import edu.bupt.tarecruitment.model.User;
import edu.bupt.tarecruitment.model.UserRole;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
        setBackground(UiTheme.PAGE_BG);

        JPanel card = new JPanel(new BorderLayout());
        UiTheme.stylePanel(card);
        card.setPreferredSize(new Dimension(1060, 620));

        JPanel introPanel = buildIntroPanel();
        JPanel formPanel = buildFormPanel();

        card.add(introPanel, BorderLayout.WEST);
        card.add(formPanel, BorderLayout.CENTER);

        add(card);
    }

    private JPanel buildIntroPanel() {
        JPanel introPanel = new JPanel();
        introPanel.setPreferredSize(new Dimension(420, 0));
        introPanel.setBackground(UiTheme.BRAND);
        introPanel.setLayout(new BoxLayout(introPanel, BoxLayout.Y_AXIS));
        introPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JLabel brand = new JLabel("TA System");
        brand.setForeground(Color.WHITE);
        brand.setFont(UiTheme.HEADING_FONT);

        JLabel caption = new JLabel("Teaching Assistant Recruitment");
        caption.setForeground(new Color(219, 234, 254));
        caption.setFont(UiTheme.SMALL_FONT);

        JLabel title = new JLabel("Welcome back");
        title.setForeground(Color.WHITE);
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 30));

        JLabel line1 = new JLabel("Manage applications, publish positions,");
        JLabel line2 = new JLabel("and keep module staffing balanced.");
        line1.setForeground(new Color(219, 234, 254));
        line2.setForeground(new Color(219, 234, 254));
        line1.setFont(UiTheme.BODY_FONT);
        line2.setFont(UiTheme.BODY_FONT);

        introPanel.add(brand);
        introPanel.add(Box.createVerticalStrut(6));
        introPanel.add(caption);
        introPanel.add(Box.createVerticalGlue());
        introPanel.add(title);
        introPanel.add(Box.createVerticalStrut(16));
        introPanel.add(line1);
        introPanel.add(Box.createVerticalStrut(8));
        introPanel.add(line2);
        introPanel.add(Box.createVerticalStrut(20));
        introPanel.add(exampleLine("TA demo: ta1 / ta123"));
        introPanel.add(Box.createVerticalStrut(8));
        introPanel.add(exampleLine("MO demo: mo1 / mo123"));
        introPanel.add(Box.createVerticalGlue());

        return introPanel;
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UiTheme.SURFACE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(34, 34, 34, 34));

        UiTheme.styleField(usernameField);
        UiTheme.styleField(passwordField);
        UiTheme.styleComboBox(roleBox);

        JButton loginButton = new JButton("Login");
        UiTheme.stylePrimaryButton(loginButton);
        loginButton.addActionListener(event -> handleLogin());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        formPanel.add(UiTheme.pageTitle("TA Recruitment Login"), constraints);

        constraints.gridy = 1;
        formPanel.add(UiTheme.mutedLabel("Sign in with your current system role."), constraints);

        constraints.gridy = 2;
        formPanel.add(UiTheme.badge("Swing UI"), constraints);

        constraints.gridy = 3;
        formPanel.add(label("Username"), constraints);

        constraints.gridy = 4;
        formPanel.add(usernameField, constraints);

        constraints.gridy = 5;
        formPanel.add(label("Password"), constraints);

        constraints.gridy = 6;
        formPanel.add(passwordField, constraints);

        constraints.gridy = 7;
        formPanel.add(label("Role"), constraints);

        constraints.gridy = 8;
        formPanel.add(roleBox, constraints);

        constraints.gridy = 9;
        JLabel helper = new JLabel("<html>First-time TA users should complete their profile before applying for positions.</html>");
        helper.setOpaque(true);
        helper.setBackground(UiTheme.BRAND_SOFT);
        helper.setForeground(UiTheme.BRAND);
        helper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        helper.setFont(UiTheme.BODY_FONT);
        helper.setPreferredSize(new Dimension(420, 60));
        formPanel.add(helper, constraints);

        constraints.gridy = 10;
        formPanel.add(loginButton, constraints);

        constraints.gridy = 11;
        constraints.weighty = 1.0;
        formPanel.add(new JPanel(), constraints);

        return formPanel;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    private JPanel exampleLine(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        JLabel label = new JLabel(text);
        label.setForeground(new Color(219, 234, 254));
        label.setFont(UiTheme.BODY_FONT);
        panel.add(label);
        return panel;
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
            showErrorDialog(exception.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(
                this,
                "<html><div style='width:260px;'>" + message + "</div></html>",
                "Login Failed",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]
        );
    }
}
