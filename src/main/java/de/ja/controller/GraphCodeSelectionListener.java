package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.ExplainerFrame;
import de.ja.view.editor.OriginalAssetPanel;
import de.ja.view.table.GraphCodeTable;
import de.swa.gc.GraphCode;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Diese Klasse stellt das Steuerelement zum Behandeln
 * der Auswahl eines Graph Codes aus der Liste und der daraus
 * folgenden Interaktionen mit anderen Benutzerschnittstellen dar.
 */
public class GraphCodeSelectionListener implements ListSelectionListener {

    // Referenz.
    private final ExplainerFrame reference;

    public GraphCodeSelectionListener(ExplainerFrame reference) {
        this.reference = reference;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            JList<GraphCodeListElement> graphCodeList = reference.getEditorGraphCode().getGraphCodeList();
            // Benutzerschnittstelle zur tabellarischen
            // Darstellung eines Graph Codes.
            GraphCodeTable graphCodeTable = reference.getEditorGraphCode().getGraphCodeTable();
            // Benutzerschnittstelle zur Darstellung der mit
            // dem Graph Code original assoziierten Datei
            OriginalAssetPanel originalAssetPanel = reference.getEditorGraphCode().getOriginalAssetPanel();
            // Ausgewähltes Element.
            GraphCodeListElement graphCodeListElement = graphCodeList.getSelectedValue();
            JLabel graphCodeNameLabel = reference.getEditorGraphCode().getGraphCodeName();
            int selectionSize = graphCodeList.getSelectedValuesList().size();
            if(selectionSize > 2) {
                graphCodeNameLabel.setText("Multiple Selection");
            }
            if(graphCodeListElement != null) {
                GraphCode gc = graphCodeListElement.getGraphCode();
                graphCodeNameLabel.setText(graphCodeListElement.getFileName());
                // Graph Code delegieren.
                graphCodeTable.setGraphCode(gc);
                originalAssetPanel.searchForSimilarFile(graphCodeListElement.getFileName());
                // Graph Code delegieren.
                reference.getExplanationPanel().setGraphCode(gc);
            } else {
                // Auswahl in Benutzerschnittstellen zurücksetzen.
                graphCodeNameLabel.setText("");
                graphCodeTable.setGraphCode(null);
                originalAssetPanel.searchForSimilarFile(null);
            }
        }
    }
}
