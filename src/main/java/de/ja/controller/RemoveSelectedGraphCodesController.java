package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.editor.EditorGraphCode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diese Klasse stellt das Steuerelement zum Entfernen von
 * Graph Code Dateien aus der Arbeitsfläche bzw. Liste dar.
 */
public class RemoveSelectedGraphCodesController implements ActionListener {

    // Referenz.
    private final EditorGraphCode reference;

    public RemoveSelectedGraphCodesController(EditorGraphCode reference) {
        this.reference = reference;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Benutzerschnittstelle für die Liste an Graph Code Dateien.
        JList<GraphCodeListElement> graphCodeList = reference.getGraphCodeList();
        // Datenmodell für die Liste.
        DefaultListModel<GraphCodeListElement> dlm = reference.getGraphCodeListModel();
        // Ausgewählte Indizes.
        int[] selIndices = graphCodeList.getSelectedIndices();
        if(selIndices.length > 0) {
            // Für alle Indizes jedes entsprechende Element löschen.
            for(int i = selIndices.length - 1; i >= 0; i--) {
                dlm.removeElementAt(selIndices[i]);
            }
        }
    }
}
