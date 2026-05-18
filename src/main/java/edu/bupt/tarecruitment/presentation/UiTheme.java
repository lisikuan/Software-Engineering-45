package edu.bupt.tarecruitment.presentation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

final class UiTheme {
    static final Color PAGE_BG = new Color(244, 247, 251);
    static final Color SURFACE = Color.WHITE;
    static final Color SURFACE_ALT = new Color(248, 250, 252);
    static final Color LINE = new Color(226, 232, 240);
    static final Color LINE_STRONG = new Color(203, 213, 225);
    static final Color TEXT = new Color(30, 41, 59);
    static final Color TITLE = new Color(23, 37, 84);
    static final Color MUTED = new Color(100, 116, 139);
    static final Color BRAND = new Color(37, 99, 235);
    static final Color BRAND_SOFT = new Color(239, 246, 255);
    static final Color SUCCESS = new Color(16, 185, 129);
    static final Color WARNING = new Color(245, 158, 11);
    static final Color DANGER = new Color(239, 68, 68);

    static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private UiTheme() {
    }

    static void installDefaults() {
        UIManager.put("Panel.background", PAGE_BG);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.font", BODY_FONT);
        UIManager.put("TextField.font", BODY_FONT);
        UIManager.put("PasswordField.font", BODY_FONT);
        UIManager.put("ComboBox.font", BODY_FONT);
        UIManager.put("Table.font", BODY_FONT);
    }

    static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );
    }

    static Border sectionBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 16, 16)
        );
    }

    static JLabel pageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TITLE);
        return label;
    }

    static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        label.setForeground(TITLE);
        return label;
    }

    static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SMALL_FONT);
        label.setForeground(MUTED);
        return label;
    }

    static JLabel badge(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(BRAND_SOFT);
        label.setForeground(BRAND);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return label;
    }

    static JLabel statusBadge(String text, Color background, Color foreground) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return label;
    }

    static void stylePanel(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(SURFACE);
        panel.setBorder(cardBorder());
    }

    static void styleSection(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(SURFACE);
        panel.setBorder(sectionBorder());
    }

    static void styleField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setCaretColor(TITLE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_STRONG, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
    }

    static void styleTextArea(JTextArea area) {
        area.setFont(BODY_FONT);
        area.setForeground(TEXT);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE_STRONG, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }

    static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(BODY_FONT);
        comboBox.setForeground(TEXT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(LINE_STRONG, 1, true));
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 40));
    }

    static void stylePrimaryButton(JButton button) {
        styleButton(button, BRAND, Color.WHITE, BRAND);
    }

    static void styleSecondaryButton(JButton button) {
        styleButton(button, Color.WHITE, TITLE, LINE_STRONG);
    }

    static void styleDangerButton(JButton button) {
        styleButton(button, new Color(254, 242, 242), DANGER, new Color(254, 202, 202));
    }

    private static void styleButton(JButton button, Color background, Color foreground, Color borderColor) {
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
    }

    static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setGridColor(LINE);
        table.setSelectionBackground(BRAND_SOFT);
        table.setSelectionForeground(TITLE);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFont(BODY_FONT);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(SURFACE_ALT);
        header.setForeground(MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));
    }

    static JScrollPane styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(LINE, 1, true));
        scrollPane.getViewport().setBackground(SURFACE);
        return scrollPane;
    }

    static void styleFlatLabel(JLabel label) {
        label.setFont(BODY_FONT);
        label.setForeground(TEXT);
    }

    static void pad(JComponent component) {
        component.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    static void showInfo(Component parent, String title, String message) {
        showMessage(parent, title, message, JOptionPane.INFORMATION_MESSAGE);
    }

    static void showWarning(Component parent, String title, String message) {
        showMessage(parent, title, message, JOptionPane.WARNING_MESSAGE);
    }

    static void showError(Component parent, String title, String message) {
        showMessage(parent, title, message, JOptionPane.ERROR_MESSAGE);
    }

    private static void showMessage(Component parent, String title, String message, int type) {
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(
                parent,
                "<html><div style='width:280px;'>" + message + "</div></html>",
                title,
                JOptionPane.DEFAULT_OPTION,
                type,
                null,
                options,
                options[0]
        );
    }
}
