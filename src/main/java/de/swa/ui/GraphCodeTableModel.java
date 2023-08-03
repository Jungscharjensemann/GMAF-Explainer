package de.swa.ui;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.swa.gc.GraphCode;

/** component that provides the table model for Graph Codes **/
public class GraphCodeTableModel implements TableModel {
	private GraphCode gc;
	public GraphCodeTableModel(GraphCode gc) {
		this.gc = gc;
	}
	
	public void addTableModelListener(TableModelListener l) {
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return gc.getDictionary().size() + 1;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) return "";
		return gc.getDictionary().get(columnIndex - 1);
	}

	public int getRowCount() {
		return gc.getDictionary().size() + 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if (columnIndex == 0 && rowIndex == 0) return "";
			if (columnIndex == 0 && rowIndex > 0) return gc.getDictionary().get(rowIndex - 1);
		
			return "" + gc.getValue(rowIndex - 1, columnIndex - 1);
		}
		catch (Exception x) {
			System.out.println("GC Table Model Error " + rowIndex + " " + columnIndex);
			return "";
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void removeTableModelListener(TableModelListener l) {
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
}
