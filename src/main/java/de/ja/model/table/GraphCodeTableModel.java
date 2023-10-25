package de.ja.model.table;

import de.swa.gc.GraphCode;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * Diese Klasse stellt das Datenmodell für
 * die tabellarische Darstellung eines Graph
 * Codes dar.
 */
public class GraphCodeTableModel extends AbstractTableModel {

    // Assoziierter Graph Code.
    private GraphCode graphCode;

    public GraphCodeTableModel() {}

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public int getColumnCount() {
        return graphCode != null ? graphCode.getDictionary().size() + 1 : 0;
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) return "";
        return graphCode.getDictionary().get(columnIndex - 1);
    }

    public int getRowCount() {
        return graphCode != null ? graphCode.getDictionary().size() + 1 : 0;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        // Wert an Zeile u. Spalte zuweisen.
        try {
            if(columnIndex == 0 && rowIndex == 0) {
                return "";
            }
            if(columnIndex == 0 && rowIndex > 0) {
                return graphCode.getDictionary().get(rowIndex - 1);
            }
            if(columnIndex > 0 && rowIndex == 0) {
                return graphCode.getDictionary().get(columnIndex - 1);
            }

            return "" + graphCode.getValue(rowIndex - 1, columnIndex - 1);
        }
        catch (Exception x) {
            System.out.println("GC Table Model Error " + rowIndex + " " + columnIndex);
            return "";
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return false;
        }
        if(rowIndex == 0) {
            return false;
        }
        return true;
    }

    public void removeTableModelListener(TableModelListener l) {}

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(graphCode != null) {
            graphCode.setValue(rowIndex - 1, columnIndex - 1, Integer.parseInt((String) aValue));
        }
    }

    /**
     * Setzt aktuellen Graph Code und
     * lässt Daten aktualisiert.
     * @param graphCode Ausgewählter Graph Code.
     */
    public void setGraphCode(GraphCode graphCode) {
        this.graphCode = graphCode;
        fireTableDataChanged();
    }
}
