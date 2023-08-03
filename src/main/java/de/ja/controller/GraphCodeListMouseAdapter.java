package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.editor.EditorGraphCode;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraphCodeListMouseAdapter extends MouseAdapter {

    private final EditorGraphCode reference;

    private final JList<GraphCodeListElement> graphCodeList;

    private final DefaultListModel<GraphCodeListElement> dlm;

    public GraphCodeListMouseAdapter(EditorGraphCode reference) {
        this.reference = reference;
        this.graphCodeList = reference.getGraphCodeList();
        this.dlm = reference.getGraphCodeListModel();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Doppelklick.
        if(e.getClickCount() == 2) {
            int selIdx = graphCodeList.locationToIndex(e.getPoint());
            GraphCodeListElement element = dlm.getElementAt(selIdx);
            String rename = JOptionPane.showInputDialog(null,
                    "New Name", "Rename GraphCode", JOptionPane.PLAIN_MESSAGE);
            if(rename != null) {
                if(!rename.isEmpty()) {
                    element.setFileName(rename.trim());
                    reference.getGraphCodeName().setText(rename);
                }
            }
        }
    }
}
