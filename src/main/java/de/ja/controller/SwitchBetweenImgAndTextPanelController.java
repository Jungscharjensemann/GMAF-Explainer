package de.ja.controller;

import de.ja.view.explanation.ExplanationPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwitchBetweenImgAndTextPanelController implements ActionListener {

    private final ExplanationPanel reference;

    public SwitchBetweenImgAndTextPanelController(ExplanationPanel reference) {
        this.reference = reference;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "ImagePanel":
                break;
            case "TextPanel":
                break;
        }
    }
}
