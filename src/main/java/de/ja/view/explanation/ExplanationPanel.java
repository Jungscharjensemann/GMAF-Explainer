package de.ja.view.explanation;

import com.formdev.flatlaf.FlatDarkLaf;
import de.ja.view.ExplainerFrame;
import de.ja.view.explanation.audio.AudioPanel;
import de.ja.view.explanation.image.ImagePanel;
import de.ja.view.explanation.text.TextPanel;
import de.swa.gc.GraphCode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diese Klasse stellt den Grundbereich
 * ExplanationPanel dar.
 */
public class ExplanationPanel extends JPanel implements ActionListener {

    // Benutzerschnittstelle ImagePanel.
    private final ImagePanel imagePanel;

    // Benutzerschnittstelle TextPanel.
    private final TextPanel textPanel;

    private final AudioPanel audioPanel;

    private final BorderLayout borderLayout;

    public ExplanationPanel(ExplainerFrame reference) {
        borderLayout = new BorderLayout();
        setLayout(borderLayout);
        setBorder(new TitledBorder("Graph Code - Explanation"));

        // Panel für die Knöpfe.
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        FlatDarkLaf.install();

        UIManager.put("ToggleButton.selectedForeground", Color.white);

        // Knöpfe für Benutzerschnittstellen ImagePanel / TextPanel.
        JToggleButton imageButton = new JToggleButton("Image");
        imageButton.setActionCommand("ImagePanel");
        imageButton.addActionListener(this);
        imageButton.setSelected(true);
        imageButton.putClientProperty("JButton.buttonType", "square");

        JToggleButton textButton = new JToggleButton("Text");
        textButton.setActionCommand("TextPanel");
        textButton.addActionListener(this);
        textButton.putClientProperty("JButton.buttonType", "square");

        JToggleButton audioButton = new JToggleButton("Audio");
        audioButton.setActionCommand("AudioPanel");
        audioButton.addActionListener(this);
        textButton.putClientProperty("JButton.buttonType", "square");

        // Knöpfe gruppieren.
        ButtonGroup group = new ButtonGroup();
        group.add(imageButton);
        group.add(textButton);
        group.add(audioButton);

        tabPanel.add(imageButton);
        tabPanel.add(textButton);
        tabPanel.add(audioButton);

        imagePanel = new ImagePanel(reference);
        textPanel = new TextPanel(reference);
        audioPanel = new AudioPanel(reference);

        add(tabPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
    }

    /**
     * Diese Methode delegiert einen
     * ausgewählten Graph Code an die Benutzerschnittstellen
     * ImagePanel und TextPanel.
     * @param graphCode Ausgewählter Graph Code.
     */
    public void setGraphCode(GraphCode graphCode) {
        this.imagePanel.setGraphCode(graphCode);
        this.textPanel.setGraphCode(graphCode);
        this.audioPanel.setGraphCode(graphCode);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component centerComp = borderLayout.getLayoutComponent(this, BorderLayout.CENTER);
        if(centerComp != null) {
            remove(centerComp);
            switch(e.getActionCommand()) {
                case "ImagePanel":
                    if(!centerComp.equals(imagePanel)) {
                        centerComp = imagePanel;
                    }
                    break;
                case "TextPanel":
                    if(!centerComp.equals(textPanel)) {
                        centerComp = textPanel;
                    }
                    break;
                case "AudioPanel":
                    if(!centerComp.equals(audioPanel)) {
                        centerComp = audioPanel;
                    }
                    break;
            }
            add(centerComp, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }
}
