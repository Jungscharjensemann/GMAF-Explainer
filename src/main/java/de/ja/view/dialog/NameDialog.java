package de.ja.view.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NameDialog extends JDialog {

    private final JTextField nameField;

    public NameDialog() {
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());

        nameField = new JTextField();
        namePanel.add(nameField, BorderLayout.CENTER);

        getContentPane().add(namePanel);

        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                dispose();
            }
        });
    }

    public String getName() {
        return nameField.getText();
    }
}
