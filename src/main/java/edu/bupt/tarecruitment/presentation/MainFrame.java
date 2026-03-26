package edu.bupt.tarecruitment.presentation;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("TA Recruitment System");
        initializeUi();
    }

    private void initializeUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        add(new JLabel("TA Recruitment System Skeleton [待确认]", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
