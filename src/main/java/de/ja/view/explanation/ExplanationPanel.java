package de.ja.view.explanation;

import com.formdev.flatlaf.FlatDarkLaf;
import de.ja.view.ExplainerFrame;
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

    // Ob ImagePanel ausgewählt ist.
    private boolean isImgPanel = true;

    // Benutzerschnittstelle ImagePanel.
    private final ImagePanel imagePanel;

    // Benutzerschnittstelle TextPanel.
    private final TextPanel textPanel;

    public ExplanationPanel(ExplainerFrame reference) {
        setLayout(new BorderLayout());
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

        // Knöpfe gruppieren.
        ButtonGroup group = new ButtonGroup();
        group.add(imageButton);
        group.add(textButton);

        tabPanel.add(imageButton);
        tabPanel.add(textButton);

        imagePanel = new ImagePanel(reference);
        textPanel = new TextPanel(reference);

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "ImagePanel":
                if(!isImgPanel) {
                    remove(textPanel);
                    add(imagePanel, BorderLayout.CENTER);
                    isImgPanel = true;
                }
                revalidate();
                repaint();
                break;
            case "TextPanel":
                if(isImgPanel) {
                    remove(imagePanel);
                    add(textPanel, BorderLayout.CENTER);
                    isImgPanel = false;
                }
                revalidate();
                repaint();
                break;
        }
    }
}
