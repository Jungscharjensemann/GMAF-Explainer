package de.swa.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

/** component that provides the renderer for the Graph Codes **/
public class GraphCodeRenderer implements TableCellRenderer {
	private boolean isHeader = false;
	public GraphCodeRenderer(boolean isHeader) {
		this.isHeader = isHeader;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		boolean isQueryString = false;
		Vector<String> currentVocablary = new Vector<String>();
		try { currentVocablary = MMFGCollection.getInstance().getCurrentQuery().getDictionary(); } catch (Exception x) {}
		for (String s : currentVocablary) {
			String v = value.toString();
			if (s.indexOf(v) >= 0) isQueryString = true;
			else if (v.indexOf(s) >= 0) isQueryString = true;
		}
		if (isHeader) {
			VerticalLabel tf = new VerticalLabel("" + value);
			if (isQueryString) {
	 			tf.setBackground(Color.lightGray);
				tf.setForeground(Color.BLACK);
				tf.setOpaque(true);
			}

			return tf;
		}
		JTextField tf = new JTextField(2);
		tf.setBorder(new LineBorder(Color.black, 1));
		tf.setText("" + value);
		if (row == column) {
			tf.setBackground(Color.lightGray);
			tf.setForeground(Color.BLACK);
		}
		if (isQueryString) {
 			tf.setBackground(Color.lightGray);
			tf.setForeground(Color.BLACK);
			tf.setOpaque(true);
		}
		return tf;
	}
}

class VerticalLabel extends JLabel {
	private String text;
	
	public VerticalLabel(String t) {
		text = t;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        final AffineTransform transform = g2.getTransform();

        g2.rotate(Math.toRadians(-90));
        g2.drawString(text, 1 - 70,
                g2.getFontMetrics().getAscent());
        
        g2.setTransform(transform);        
    }
}
