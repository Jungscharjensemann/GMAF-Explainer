package de.ja.view.table.renderer;

import org.pbjar.jxlayer.plaf.ext.transform.DefaultTransformModel;
import org.pbjar.jxlayer.plaf.ext.transform.TransformUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt einen Renderer für die tabellarische
 * Darstellung eines ausgewählten Graph Codes dar.
 */
public class BetterGraphCodeRenderer extends DefaultTableCellRenderer {

    Dimension dim = new Dimension(73, 18);

    MatteBorder matteBorder = new MatteBorder(1, 1, 1, 1, Color.black);

    private final Color tFG = new Color(167, 167, 167);

    public BetterGraphCodeRenderer() {}

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        l.setBorder(new LineBorder(Color.black, 1));
        if(row == column) {
            l.setBackground(Color.lightGray);
            l.setForeground(Color.black);
        } else {
            l.setBackground(null);
            Pattern pattern = Pattern.compile("[1-9]*");
            Matcher matcher = pattern.matcher((String) value);
            if(matcher.matches()) {
                l.setForeground(Color.cyan);
            } else {
                l.setForeground(tFG);
            }
        }
        if(column > 0 && row > 0) {
            l.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            l.setHorizontalAlignment(LEFT);
        }
        if(column > 0 && row == 0) {
            JLabel vL = new JLabel(String.valueOf(value));
            vL.setPreferredSize(dim);
            vL.setBorder(matteBorder);
            vL.setHorizontalAlignment(CENTER);

            // TransformLayer zum Rotieren des Textes.
            DefaultTransformModel defaultTransformModel = new DefaultTransformModel();
            defaultTransformModel.setRotation(Math.toRadians(-90));
            JLayer<?> layer = null;
            try {
                layer = TransformUtils.createTransformJLayer(vL, defaultTransformModel);
            } catch(Exception ignored) {}
            return layer;
        }
        return l;
    }
}
