package edu.bupt.tarecruitment;

import edu.bupt.tarecruitment.common.ApplicationBootstrap;
import edu.bupt.tarecruitment.presentation.MainFrame;

import javax.swing.SwingUtilities;

public final class TaRecruitmentApplication {
    private TaRecruitmentApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new ApplicationBootstrap().createMainFrame();
            mainFrame.setVisible(true);
        });
    }
}
