package de.swa.fuh.qrefinement.ui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renders delete button in JTable
 * 
 * @author Nicolas Boch
 *
 */
public class ButtonRenderer implements TableCellRenderer
{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
	JButton b = new JButton ("Delete");
	b.setBackground(Color.WHITE);
	b.setActionCommand(String.valueOf(row));
	
		
		return b;
	}
	
	
}
