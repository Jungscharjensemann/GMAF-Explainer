package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.editor.EditorGraphCode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RemoveSelectedGraphCodesController implements ActionListener {

    private final EditorGraphCode reference;

    public RemoveSelectedGraphCodesController(EditorGraphCode reference) {
        this.reference = reference;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JList<GraphCodeListElement> graphCodeList = reference.getGraphCodeList();
        DefaultListModel<GraphCodeListElement> dlm = reference.getGraphCodeListModel();
        int[] selIndices = graphCodeList.getSelectedIndices();
        if(selIndices.length > 0) {
            for(int i = selIndices.length - 1; i >= 0; i--) {
                dlm.removeElementAt(selIndices[i]);
            }
        }
    }
}
