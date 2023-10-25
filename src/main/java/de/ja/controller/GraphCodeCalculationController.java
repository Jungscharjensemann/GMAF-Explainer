package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.ExplainerFrame;
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

/**
 * Diese Klasse stellt das Steuerelement
 * zum Differenzieren und Durchführen von Operationen
 * auf Graph Codes dar.
 */
public class GraphCodeCalculationController implements ActionListener {

    // Referenz.
    private final ExplainerFrame reference;
    public GraphCodeCalculationController(ExplainerFrame reference) {
        this.reference = reference;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditorGraphCode egc = reference.getEditorGraphCode();
        JComboBox<String> calcCmb = egc.getCalculationComboBox();
        // Ausgewählte Aktion / Operation.
        String actionItem = (String) calcCmb.getSelectedItem();

        JList<GraphCodeListElement> graphCodeList = egc.getGraphCodeList();
        DefaultListModel<GraphCodeListElement> dlm = egc.getGraphCodeListModel();
        List<GraphCodeListElement> selGraphCodes = graphCodeList.getSelectedValuesList();
        // Erstes Element ausgewählt.
        if(calcCmb.getSelectedIndex() == 0) {
            reference.getExplainerConsoleModel().insertText("Select one of the following operations: " +
                    "Union, Subtraction, Similarities, Differences!");
            return;
        }
        // Differenzierung der Operationen.
        switch(Objects.requireNonNull(actionItem)) {
            case "Union":
                reference.getExplainerConsoleModel().insertText("Calculating Union...");
                // Ausgewählte Graph Codes in Vector sammeln.
                Vector<GraphCode> vGcsUni = selGraphCodes.
                        stream().map(GraphCodeListElement::getGraphCode).
                        collect(Collectors.toCollection(Vector::new));
                // Vereinigung berechnen.
                GraphCode union = GraphCodeCollection.getUnion(vGcsUni);
                vGcsUni.forEach(union::addGraphCode);
                // Neues Element für die Liste erzeugen.
                GraphCodeListElement unionListElement = new GraphCodeListElement(union, "Union");
                // Dialog zum Benennen anzeigen.
                String unionElementName = JOptionPane.showInputDialog(null,
                        "Set Name", "Union", JOptionPane.PLAIN_MESSAGE);
                unionListElement.setFileName(unionElementName);
                // Element dem Datenmodell der Liste hinzufügen.
                dlm.add(0, unionListElement);
                break;
            case "Subtraction":
                if(selGraphCodes.size() > 1) {
                    reference.getExplainerConsoleModel().insertText("Calculating Subtraction...");
                    GraphCodeListElement gcLe1 = selGraphCodes.get(0);
                    GraphCodeListElement gcLe2 = selGraphCodes.get(1);
                    // Subtraktion berechnen.
                    GraphCode subtract = GraphCodeCollection.subtract(gcLe1.getGraphCode(), gcLe2.getGraphCode());
                    // Neues Element für die Liste erzeugen.
                    GraphCodeListElement subtractListElement = new GraphCodeListElement(subtract, "Subtract");
                    // Dialog zum Benennen anzeigen.
                    String subtractElementName = JOptionPane.showInputDialog(null,
                            "Set Name", "Subtraction", JOptionPane.PLAIN_MESSAGE);
                    subtractListElement.setFileName(subtractElementName);
                    // Element dem Datenmodell der Liste hinzufügen.
                    dlm.add(0, subtractListElement);
                } else {
                    reference.getExplainerConsoleModel().insertText("Select at least two graph codes to perform a subtraction!");
                }
                break;
            case "Similarities":
                reference.getExplainerConsoleModel().insertText("Calculating Similarities...");
                // Ausgewählte Graph Codes in Vector sammeln.
                Vector<GraphCode> vGcsSim = selGraphCodes.
                        stream().map(GraphCodeListElement::getGraphCode).
                        collect(Collectors.toCollection(Vector::new));
                // Gemeinsamkeiten berechnen.
                GraphCode sim = GraphCodeCollection.getSimilarities(vGcsSim);
                // Neues Element für die Liste erzeugen.
                GraphCodeListElement simListElement = new GraphCodeListElement(sim, "Similarities");
                // Dialog zum Benennen anzeigen.
                String simElementName = JOptionPane.showInputDialog(null,
                        "Set Name", "Similarities", JOptionPane.PLAIN_MESSAGE);
                simListElement.setFileName(simElementName);
                // Element dem Datenmodell der Liste hinzufügen.
                dlm.add(0, simListElement);
                break;
            case "Differences":
                reference.getExplainerConsoleModel().insertText("Calculating Differences...");
                // Ausgewählte Graph Codes in Vector sammeln.
                Vector<GraphCode> vGcsDiff = selGraphCodes.
                        stream().map(GraphCodeListElement::getGraphCode).
                        collect(Collectors.toCollection(Vector::new));
                // Differenz berechnen.
                GraphCode difference = GraphCodeCollection.getDifferences(vGcsDiff);
                // Neues Element für die Liste erzeugen.
                GraphCodeListElement differenceListElement = new GraphCodeListElement(difference, "Difference");
                // Dialog zum Benennen anzeigen.
                String diffElementName = JOptionPane.showInputDialog(null,
                        "Set Name", "Differences", JOptionPane.PLAIN_MESSAGE);
                differenceListElement.setFileName(diffElementName);
                // Element dem Datenmodell der Liste hinzufügen.
                dlm.add(0, differenceListElement);
                break;
        }
    }
}
