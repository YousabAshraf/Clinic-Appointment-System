package app.ui.style;

import java.awt.Color;
import java.awt.Font;

public class Theme {

    public static final Color PRIMARY_COLOR = new Color(2, 119, 189); // #0277BD - Medical Blue
    public static final Color PRIMARY_DARK = new Color(1, 87, 155); // #01579B - Deep Blue
    public static final Color ACCENT_COLOR = new Color(79, 195, 247); // #4FC3F7 - Sky Blue

    public static final Color HEALING_GREEN = new Color(102, 187, 106); // #66BB6A
    public static final Color SOFT_TEAL = new Color(38, 166, 154); // #26A69A

    public static final Color BACKGROUND_COLOR = new Color(250, 250, 250); // #FAFAFA - Off-White
    public static final Color SURFACE_COLOR = Color.WHITE;

    public static final Color WHITE = SURFACE_COLOR;
    public static final Color SECONDARY_COLOR = PRIMARY_DARK;

    public static final Color TEXT_PRIMARY = new Color(66, 66, 66); // #424242
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117); // #757575

    public static final Color SUCCESS = new Color(76, 175, 80); // #4CAF50 - Green
    public static final Color ERROR_COLOR = new Color(239, 83, 80); // #EF5350 - Red
    public static final Color WARNING = new Color(255, 167, 38); // #FFA726 - Amber

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);


    public static void styleTable(javax.swing.JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(227, 242, 253)); // Light Blue Selection
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(224, 224, 224));

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(SUBHEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new java.awt.Dimension(0, 40));
    }

    public static void styleButton(javax.swing.JButton btn, boolean isPrimary) {
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        if (isPrimary) {
            btn.setBackground(PRIMARY_COLOR);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(SURFACE_COLOR);
            btn.setForeground(PRIMARY_COLOR);
            btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                    javax.swing.BorderFactory.createEmptyBorder(9, 19, 9, 19)));
        }

    }
}
