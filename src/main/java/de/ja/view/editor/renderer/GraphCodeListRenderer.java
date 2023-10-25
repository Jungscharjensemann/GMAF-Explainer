package de.ja.view.editor.renderer;

import de.ja.model.editor.GraphCodeListElement;

import javax.swing.*;
import java.awt.*;

/**
 * Diese Klasse stellt den Renderer für
 * die Elemente in der Liste dar.
 */
public class GraphCodeListRenderer extends JLabel implements ListCellRenderer<GraphCodeListElement> {

    public GraphCodeListRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GraphCodeListElement> list,
                                                  GraphCodeListElement graphCodeListElement,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        ImageIcon icon = new ImageIcon("resources/document_tag.png");
        setBorder(BorderFactory.createEmptyBorder());
        setIcon(new ImageIcon(icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        setText(graphCodeListElement.getFileName());
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(BOTTOM);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setPreferredSize(new Dimension(50, 50));

        return this;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(64, 64);
    }
}
