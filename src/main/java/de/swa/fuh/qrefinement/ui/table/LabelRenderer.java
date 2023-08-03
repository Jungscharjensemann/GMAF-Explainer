package de.swa.fuh.qrefinement.ui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 * Implements rendering of table cell with colors green and red
 * 
 * @author Nicolas Boch
 *
 */
public class LabelRenderer implements TableCellRenderer
{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JTextField t = new JTextField();
		
		if (((String) value).equals("green")){
			t.setBackground(Color.GREEN);
		}
		if (((String) value).equals("red")){
			t.setBackground(Color.RED);
		}
		if (((String) value).equals("")){
			t.setBackground(Color.WHITE);
		}
		
		return t;
	}
	
}
