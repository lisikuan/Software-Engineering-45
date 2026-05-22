package edu.bupt.tarecruitment.presentation;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

final class DashboardShell {
    record NavItem(String index, String label, boolean active, Runnable action) {
        NavItem(String index, String label, boolean active) {
            this(index, label, active, null);
        }
    }
    static final class StatCard {
        private final JPanel panel;
        private final JLabel valueLabel;

        private StatCard(String label, String value) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            UiTheme.styleSection(panel);

            JLabel titleLabel = UiTheme.mutedLabel(label);
            valueLabel = new JLabel(value);
            valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
            valueLabel.setForeground(UiTheme.TITLE);

            panel.add(titleLabel);
            panel.add(Box.createVerticalStrut(10));
            panel.add(valueLabel);
        }

        JPanel panel() {
            return panel;
        }

        void setValue(String value) {
            valueLabel.setText(value);
        }
    }

    private DashboardShell() {
    }

    static JPanel buildShell(
            String portalLabel,
            List<NavItem> navItems,
            String userName,
            String userRole,
            String pageTitle,
            String pageSubtitle,
            JPanel mainContent
    ) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiTheme.PAGE_BG);

        root.add(buildSidebar(portalLabel, navItems), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(buildTopBar(userName, userRole), BorderLayout.NORTH);

        JPanel contentWrap = new JPanel(new BorderLayout(0, 22));
        contentWrap.setOpaque(false);
        contentWrap.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));
        contentWrap.add(buildPageHeading(pageTitle, pageSubtitle), BorderLayout.NORTH);
        contentWrap.add(mainContent, BorderLayout.CENTER);

        main.add(contentWrap, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        return root;
    }

    static JPanel buildPageHeading(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(UiTheme.pageTitle(title));
        panel.add(Box.createVerticalStrut(8));
        panel.add(UiTheme.mutedLabel(subtitle));
        return panel;
    }

    static JPanel buildStatsRow(String[][] stats) {
        StatCard[] cards = new StatCard[stats.length];
        for (int i = 0; i < stats.length; i++) {
            cards[i] = statCard(stats[i][0], stats[i][1]);
        }
        return buildStatsRow(cards);
    }

    static StatCard statCard(String label, String value) {
        return new StatCard(label, value);
    }

    static JPanel buildStatsRow(StatCard... cards) {
        JPanel panel = new JPanel(new GridLayout(1, cards.length, 14, 0));
        panel.setOpaque(false);
        for (StatCard card : cards) {
            panel.add(card.panel());
        }
        return panel;
    }

    private static JPanel buildSidebar(String portalLabel, List<NavItem> navItems) {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(UiTheme.SURFACE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiTheme.LINE));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        brand.setOpaque(false);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        brand.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JLabel badge = new JLabel("TA", JLabel.CENTER);
        badge.setOpaque(true);
        badge.setBackground(UiTheme.BRAND);
        badge.setForeground(Color.WHITE);
        badge.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        badge.setPreferredSize(new Dimension(36, 36));
        badge.setMaximumSize(new Dimension(36, 36));

        JPanel words = new JPanel();
        words.setOpaque(false);
        words.setLayout(new BoxLayout(words, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("System");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 17));
        title.setForeground(UiTheme.TITLE);
        JLabel caption = UiTheme.mutedLabel(portalLabel);
        words.add(title);
        words.add(Box.createVerticalStrut(4));
        words.add(caption);

        brand.add(badge);
        brand.add(words);
        sidebar.add(brand);

        for (NavItem item : navItems) {
            sidebar.add(buildNavButton(item));
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());
        JLabel footer = UiTheme.mutedLabel("TA Recruitment System");
        footer.setBorder(BorderFactory.createEmptyBorder(0, 20, 18, 20));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);
        return sidebar;
    }

    private static JPanel buildNavButton(NavItem item) {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        nav.setPreferredSize(new Dimension(196, 42));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        nav.setBackground(item.active() ? UiTheme.BRAND_SOFT : UiTheme.SURFACE);

        JLabel index = new JLabel(item.index());
        index.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        index.setForeground(item.active() ? UiTheme.BRAND : UiTheme.TITLE);
        JLabel text = new JLabel(item.label());
        text.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        text.setForeground(item.active() ? UiTheme.BRAND : UiTheme.TITLE);

        nav.add(index);
        nav.add(text);
        if (item.action() != null) {
            installClick(nav, item.action());
            installClick(index, item.action());
            installClick(text, item.action());
        }
        return nav;
    }

    private static void installClick(Component component, Runnable action) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                action.run();
            }
        });
    }

    private static JPanel buildTopBar(String userName, String userRole) {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(UiTheme.SURFACE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.LINE),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        JLabel bell = new JLabel("N");
        bell.setForeground(UiTheme.MUTED);
        bell.setFont(UiTheme.BODY_FONT);
        topbar.add(bell, BorderLayout.WEST);

        JPanel user = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        user.setOpaque(false);

        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(userName);
        name.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        name.setForeground(UiTheme.TITLE);
        JLabel role = UiTheme.mutedLabel(userRole);
        userText.add(name);
        userText.add(role);

        JButton avatar = new JButton(userName.substring(0, 1).toUpperCase());
        avatar.setEnabled(false);
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setBackground(UiTheme.BRAND_SOFT);
        avatar.setForeground(UiTheme.BRAND);
        avatar.setBorder(BorderFactory.createLineBorder(UiTheme.BRAND_SOFT, 1, true));

        user.add(userText);
        user.add(avatar);
        topbar.add(user, BorderLayout.EAST);
        return topbar;
    }
}
