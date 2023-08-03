package de.ja.view.explanation;

import com.formdev.flatlaf.FlatDarkLaf;
import de.ja.view.explanation.image.ImagePanel;
import de.ja.view.explanation.text.TextPanel;
import de.swa.gc.GraphCode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExplanationPanel extends JPanel implements ActionListener {

    private GraphCode graphCode;

    private boolean isImgPanel = true;

    private final ImagePanel imagePanel;

    private final TextPanel textPanel;

    private final JToggleButton imageButton;

    private final JToggleButton textButton;

    public ExplanationPanel() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Graph Code - Explanation"));

        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        FlatDarkLaf.install();

        UIManager.put("ToggleButton.selectedForeground", Color.white);

        imageButton = new JToggleButton("Image");
        imageButton.setActionCommand("ImagePanel");
        imageButton.addActionListener(this);
        imageButton.setSelected(true);
        imageButton.putClientProperty("JButton.buttonType", "square");

        textButton = new JToggleButton("Text");
        textButton.setActionCommand("TextPanel");
        textButton.addActionListener(this);
        textButton.putClientProperty("JButton.buttonType", "square");

        ButtonGroup group = new ButtonGroup();
        group.add(imageButton);
        group.add(textButton);

        tabPanel.add(imageButton);
        tabPanel.add(textButton);

        imagePanel = new ImagePanel();
        textPanel = new TextPanel();

        add(tabPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
    }

    public void setGraphCode(GraphCode graphCode) {
        this.graphCode = graphCode;
        this.imagePanel.setGraphCode(graphCode);
        this.textPanel.setGraphCode(graphCode);
    }

    public JToggleButton getImageButton() {
        return imageButton;
    }

    public JToggleButton getTextButton() {
        return textButton;
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
