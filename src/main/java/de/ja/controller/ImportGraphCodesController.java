package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.editor.EditorGraphCode;
import de.ja.view.filechooser.GraphCodeFilter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

/**
 * Diese Klasse stellt das Steuerelement zum Importieren
 * von Graph Code Dateien in die Arbeitsfläche bzw. Liste dar.
 */
public class ImportGraphCodesController implements ActionListener {

    // Referenz.
    private final EditorGraphCode reference;

    public ImportGraphCodesController(EditorGraphCode reference) {
        this.reference = reference;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Systemweiter Auswahldialog.
        JFileChooser fileChooser = new JFileChooser();
        // Auswahldialog konfigurieren.
        fileChooser.setCurrentDirectory(new File("./graphcodes"));
        // Dateifilter für Graph Code Dateien.
        GraphCodeFilter graphCodeFilter = new GraphCodeFilter(".gc", ".gc (Graph Code)");
        DefaultListModel<GraphCodeListElement> graphCodeListeModel = reference.getGraphCodeListModel();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(graphCodeFilter);
        // Dialog zeigen.
        int openDialog = fileChooser.showOpenDialog(null);
        if(openDialog == JFileChooser.OPEN_DIALOG) {
            // Ausgewählte Dateien.
            File[] f = fileChooser.getSelectedFiles();
            // Für jede Datei neues Element der Liste über
            // das Datenmodell hinzufügen.
            Arrays.asList(f).forEach(file -> graphCodeListeModel.addElement(new GraphCodeListElement(file)));
        }
    }
}
