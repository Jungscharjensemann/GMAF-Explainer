package de.ja.view.table;

import de.ja.model.table.GraphCodeTableModel;
import de.ja.view.table.renderer.BetterGraphCodeRenderer;
import de.swa.gc.GraphCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * Diese Klasse stellt die tabellarische Darstellung
 * eines ausgewählten Graph Codes dar.
 */
public class GraphCodeTable extends JPanel {

    // Platzhalter.
    private final JLabel jTablePlaceHolder;

    // Datenmodell der Tabelle.
    private final GraphCodeTableModel graphCodeTableModel;

    // Tabelle zur Darstellung eines ausgewählten Graph Codes.
    private final JTable graphCodeTable;

    private final JScrollPane graphCodeTableSP;

    public GraphCodeTable() {
        setLayout(new BorderLayout());

        /*
         * Komponenten vorbereiten
         */

        // GraphCodeTable initialisieren und konfigurieren.
        graphCodeTable = new JTable();
        graphCodeTableModel = new GraphCodeTableModel();
        graphCodeTable.setModel(graphCodeTableModel);
        graphCodeTable.setDefaultRenderer(String.class, new BetterGraphCodeRenderer());
        graphCodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        graphCodeTable.setTableHeader(null);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new GridBagLayout());
        tablePanel.add(graphCodeTable);

        graphCodeTableSP = new JScrollPane(tablePanel);
        graphCodeTableSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        graphCodeTableSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        graphCodeTableSP.getVerticalScrollBar().setUnitIncrement(16);
        graphCodeTableSP.getHorizontalScrollBar().setUnitIncrement(16);

        jTablePlaceHolder = new JLabel("No GraphCode selected!");
        jTablePlaceHolder.setBorder(new EmptyBorder(5, 5, 5, 5));
        jTablePlaceHolder.setHorizontalAlignment(SwingConstants.CENTER);
        jTablePlaceHolder.setHorizontalTextPosition(SwingConstants.CENTER);
        add(jTablePlaceHolder, BorderLayout.CENTER);
    }

    /**
     * Diese Methode verarbeitet den
     * ausgewählten Graph Code und informiert das
     * Datenmodell über die neuen Informationen.
     * @param graphCode Ausgewählter Graph Code.
     */
    public void setGraphCode(GraphCode graphCode) {
        if(graphCode != null) {
            remove(jTablePlaceHolder);
            // Graph Code an Datenmodell delegieren.
            graphCodeTableModel.setGraphCode(graphCode);
            // Datenmodell über Änderung informieren.
            graphCodeTableModel.fireTableStructureChanged();
            // Tabellarische Darstellung konfigurieren.
            graphCodeTable.setRowHeight(0, 60);
            graphCodeTable.getColumnModel().getColumn(0).setMaxWidth(50);
            for (int i = 1; i < graphCodeTable.getColumnModel().getColumnCount(); i++) {
                graphCodeTable.getColumnModel().getColumn(i).setMaxWidth(12);
            }
            // Tabelle der Benutzerschnittstelle hinzufügen.
            add(graphCodeTableSP, BorderLayout.CENTER);
        } else {
            if(graphCodeTable != null) {
                // Tabelle entfernen und Datenmodell informieren.
                remove(graphCodeTableSP);
                graphCodeTableModel.setGraphCode(null);
            }
            // Platzhalter hinzufügen.
            addPlaceHolder();
        }
        revalidate();
        repaint();
    }

    /**
     * Diese Methode fügt einen Platzhalter
     * zum Anzeigen von Informationen der Benutzerschnittstelle
     * hinzu.
     */
    private void addPlaceHolder() {
        if(!Arrays.asList(getComponents()).contains(jTablePlaceHolder)) {
            add(jTablePlaceHolder, BorderLayout.CENTER);
        }
    }
}
