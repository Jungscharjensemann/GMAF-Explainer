package de.ja.view.table.renderer;

import org.pbjar.jxlayer.plaf.ext.transform.DefaultTransformModel;
import org.pbjar.jxlayer.plaf.ext.transform.TransformUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class GraphCodeRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

    private boolean isHeader = false;

    public GraphCodeRenderer(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JTextField cellTf = new JTextField(" " + value);
        cellTf.setBorder(new LineBorder(Color.black, 1));
        if(row == column) {
            cellTf.setBackground(Color.lightGray);
            cellTf.setForeground(Color.black);
        }
        if(column > 0 && row > 0) {
            cellTf.setHorizontalAlignment(SwingConstants.CENTER);
        }
        if(column > 0 && row == 0) {
            cellTf.setPreferredSize(new Dimension(73, cellTf.getPreferredSize().height));
            cellTf.setBorder(new MatteBorder(1, 1, 1, 1, Color.black));

            DefaultTransformModel defaultTransformModel = new DefaultTransformModel();
            defaultTransformModel.setRotation(Math.toRadians(-90));
            return TransformUtils.createTransformJLayer(cellTf, defaultTransformModel);
        }

        return cellTf;


        //setBorder(new LineBorder(Color.black, 1));
        /*setText(String.valueOf(value));
        if(row == column) {
            if(column != 0) {
                setBackground(Color.lightGray);
                setForeground(Color.BLACK);
            }
        }
        if(column > 0 && row == 0) {
            setTextRotation(3 * Math.PI / 2);
        } else {
            //setTextRotation(-Math.PI);
            setTextRotation(JXLabel.HORIZONTAL);
        }*/

        /*JTextField tf = new JTextField(2);
        tf.setBorder(new LineBorder(Color.black, 1));
        tf.setText("" + value);
        if (row == column) {
            if(column != 0) {
                tf.setBackground(Color.lightGray);
                tf.setForeground(Color.BLACK);
            }
        }
        return isHeader ? new VerticalLabel(String.valueOf(value)) : tf;
        //return isHeader ? tf : tf;*/

        /*JXLabel label = new JXLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(String.valueOf(value));
        label.setBorder(new LineBorder(Color.black, 1));
        label.setPreferredSize(new Dimension(12, 60));
        if(row == column) {
            if(column == 0) {
                label.setBorder(new MatteBorder(0, 0, 2, 2, Color.black));
            }
            label.setOpaque(true);
            label.setBackground(Color.lightGray);
            label.setForeground(Color.BLACK);
        }
        if(row > 0 && column == 0) {
            label.setBorder(null);
        }
        /*if(column > 0 && row == 0) {
            label.setTextRotation(3 * Math.PI / 2);
            label.setText("  " + value);
            label.setVerticalAlignment(SwingConstants.CENTER);

            label.setPreferredSize(new Dimension(60, 14));
            label.setBorder(null);
            //EmptyBorder emptyBorder = new EmptyBorder(2, 0, 1, 0);
            //LineBorder lineBorder = new LineBorder(Color.black, 1);
            //label.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
            /*label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.black, 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));

            DefaultTransformModel defaultTransformModel = new DefaultTransformModel();
            defaultTransformModel.setRotation(Math.toRadians(-90));
            defaultTransformModel.setScaleToPreferredSize(true);

            return TransformUtils.createTransformJLayer(label, defaultTransformModel);

            /*VerticalLabel verticalLabel = new VerticalLabel();
            verticalLabel.setText(String.valueOf(value));
            verticalLabel.setHorizontalAlignment(SwingConstants.CENTER);
            verticalLabel.setBorder(new LineBorder(Color.black, 1));
            return verticalLabel;
        } else {
            label.setTextRotation(SwingConstants.HORIZONTAL);
        }
        return label; */

        /*JXLabel xL = new JXLabel();
        xL.setHorizontalAlignment(SwingConstants.CENTER);
        xL.setBorder(new LineBorder(Color.black, 1));
        xL.setText(String.valueOf(value));
        if(row == column) {
            VerticalLabel vL = new VerticalLabel(String.valueOf(value));
            vL.setOpaque(true);
            vL.setBackground(Color.lightGray);
            vL.setForeground(Color.BLACK);
            return vL;
        }
        if(column > 0 && row == 0) {
            xL.setTextRotation(3 * Math.PI / 2);
        } else {
            xL.setTextRotation(SwingConstants.HORIZONTAL);
        }
        return xL;*/
    }

}
