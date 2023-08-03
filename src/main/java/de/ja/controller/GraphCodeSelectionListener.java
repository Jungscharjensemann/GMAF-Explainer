package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.ExplainerFrame;
import de.ja.view.table.GraphCodeTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GraphCodeSelectionListener implements ListSelectionListener {

    private final ExplainerFrame reference;

    public GraphCodeSelectionListener(ExplainerFrame reference) {
        this.reference = reference;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            JList<GraphCodeListElement> graphCodeList = reference.getEditorGraphCode().getGraphCodeList();
            GraphCodeTable graphCodeTable = reference.getEditorGraphCode().getGraphCodeTable();
            GraphCodeListElement graphCodeListElement = graphCodeList.getSelectedValue();
            JLabel graphCodeNameLabel = reference.getEditorGraphCode().getGraphCodeName();
            int selectionSize = graphCodeList.getSelectedValuesList().size();
            if(selectionSize > 2) {
                graphCodeNameLabel.setText("Multiple Selection");
            }
            if(graphCodeListElement != null) {
                graphCodeNameLabel.setText(graphCodeListElement.getFileName());
                graphCodeTable.setGraphCode(graphCodeListElement.getGraphCode());
                reference.getExplanationPanel().setGraphCode(graphCodeListElement.getGraphCode());
            } else {
                graphCodeNameLabel.setText("");
                graphCodeTable.setGraphCode(null);
            }
        }
    }
}
