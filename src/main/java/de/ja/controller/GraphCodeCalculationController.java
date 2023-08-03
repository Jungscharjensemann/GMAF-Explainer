package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.dialog.NameDialog;
import de.ja.view.editor.EditorGraphCode;
import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

public class GraphCodeCalculationController implements ActionListener {
    private final EditorGraphCode reference;
    public GraphCodeCalculationController(EditorGraphCode reference) {
        this.reference = reference;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> calcCmb = reference.getCalculationComboBox();
        String actionItem = (String) calcCmb.getSelectedItem();

        JList<GraphCodeListElement> graphCodeList = reference.getGraphCodeList();
        DefaultListModel<GraphCodeListElement> dlm = reference.getGraphCodeListModel();

        List<GraphCodeListElement> selGraphCodes = graphCodeList.getSelectedValuesList();

        switch(Objects.requireNonNull(actionItem)) {
            case "Union":
                Vector<GraphCode> vGcsUni = selGraphCodes.
                        stream().map(GraphCodeListElement::getGraphCode).
                        collect(Collectors.toCollection(Vector::new));
                GraphCode union = GraphCodeCollection.getUnion(vGcsUni);
                GraphCodeListElement unionListElement = new GraphCodeListElement(union, "Union");
                String unionElementName = JOptionPane.showInputDialog(null,
                        "Set Name", "Union", JOptionPane.PLAIN_MESSAGE);
                unionListElement.setFileName(unionElementName);
                dlm.add(0, unionListElement);
                break;
            case "Subtraction":
                GraphCodeListElement gcLe1 = selGraphCodes.get(0);
                GraphCodeListElement gcLe2 = selGraphCodes.get(1);
                GraphCode subtract = GraphCodeCollection.subtract(gcLe1.getGraphCode(), gcLe2.getGraphCode());
                GraphCodeListElement subtractListElement = new GraphCodeListElement(subtract, "Subtract");
                String subtractElementName = JOptionPane.showInputDialog(null,
                        "Set Name", "Subtraction", JOptionPane.PLAIN_MESSAGE);
                subtractListElement.setFileName(subtractElementName);
                dlm.add(0, subtractListElement);
                break;
            case "Similarities":
                Vector<GraphCode> vGcsSim = selGraphCodes.
                        stream().map(GraphCodeListElement::getGraphCode).
                        collect(Collectors.toCollection(Vector::new));
                GraphCode sim = GraphCodeCollection.getSimilarities(vGcsSim);
                GraphCodeListElement simListElement = new GraphCodeListElement(sim, "Similarities");
                String simElementName = JOptionPane.showInputDialog(null,
                        "Set Name", "Similarities", JOptionPane.PLAIN_MESSAGE);
                simListElement.setFileName(simElementName);
                dlm.add(0, simListElement);
                break;
            case "Differences":
                Vector<GraphCode> vGcsDiff = selGraphCodes.
                        stream().map(GraphCodeListElement::getGraphCode).
                        collect(Collectors.toCollection(Vector::new));
                GraphCode difference = GraphCodeCollection.getDifferences(vGcsDiff);
                GraphCodeListElement differenceListElement = new GraphCodeListElement(difference, "Difference");
                String diffElementName = JOptionPane.showInputDialog(null,
                        "Set Name", "Differences", JOptionPane.PLAIN_MESSAGE);
                differenceListElement.setFileName(diffElementName);
                dlm.add(0, differenceListElement);
                break;
        }
    }
}
