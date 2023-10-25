package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.editor.EditorGraphCode;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Diese Klasse stellt das Steuerelement
 * zum Interagieren mit Elementen bzw. Graph Codes
 * in der Liste dar.
 */
public class GraphCodeListMouseAdapter extends MouseAdapter {

    // Referenz.
    private final EditorGraphCode reference;

    // Liste an Graph Codes.
    private final JList<GraphCodeListElement> gcl;

    // Datenmodell der Liste.
    private final DefaultListModel<GraphCodeListElement> dlm;

    public GraphCodeListMouseAdapter(EditorGraphCode reference) {
        this.reference = reference;
        this.gcl = reference.getGraphCodeList();
        this.dlm = reference.getGraphCodeListModel();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Doppelklick.
        if(e.getClickCount() == 2) {
            // Ausgewählter Index in der Liste.
            int selIdx = gcl.locationToIndex(e.getPoint());
            if(!dlm.isEmpty()) {
                // Ausgewähltes Element in der Liste.
                GraphCodeListElement element = dlm.getElementAt(selIdx);
                // Dialog zum Umbenennen.
                String rename = JOptionPane.showInputDialog(null,
                        "New Name", "Rename GraphCode", JOptionPane.PLAIN_MESSAGE);
                // Namen aktualisieren.
                if(rename != null && !rename.isEmpty()) {
                    element.setFileName(rename.trim());
                    reference.getGraphCodeName().setText(rename);
                }
            }
        }
    }
}
