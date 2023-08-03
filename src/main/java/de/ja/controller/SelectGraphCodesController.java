package de.ja.controller;

import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.editor.EditorGraphCode;
import de.ja.view.filechooser.GraphCodeFilter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

public class SelectGraphCodesController implements ActionListener {

    private final EditorGraphCode reference;

    public SelectGraphCodesController(EditorGraphCode reference) {
        this.reference = reference;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./graphcodes"));
        GraphCodeFilter graphCodeFilter = new GraphCodeFilter(".gc", ".gc (Graph Code)");
        DefaultListModel<GraphCodeListElement> graphCodeListeModel = reference.getGraphCodeListModel();
        fileChooser.addChoosableFileFilter(graphCodeFilter);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(graphCodeFilter);
        int openDialog = fileChooser.showOpenDialog(null);
        if(openDialog == JFileChooser.OPEN_DIALOG) {
            File[] f = fileChooser.getSelectedFiles();
            //Arrays.asList(f).forEach(file -> System.out.println(file.getName()));
            Arrays.asList(f).forEach(file -> {
                graphCodeListeModel.addElement(new GraphCodeListElement(file));
            });
        } else {

        }
    }
}
