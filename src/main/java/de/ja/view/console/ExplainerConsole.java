package de.ja.view.console;

import de.ja.model.console.ExplainerConsoleModel;
import de.ja.model.console.ITextInsertListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ExplainerConsole extends JPanel implements ITextInsertListener {

    private final RSyntaxTextArea rSyntaxTextArea;

    public ExplainerConsole(ExplainerConsoleModel model) {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Konsole"));
        setMinimumSize(new Dimension(100, 100));

        if(model != null) {
            model.addListener(this);
        }

        rSyntaxTextArea = new RSyntaxTextArea();
        rSyntaxTextArea.setEOLMarkersVisible(false);
        //rSyntaxTextArea.setBackground(new Color(69, 73, 74));
        rSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        rSyntaxTextArea.setCodeFoldingEnabled(true);
        rSyntaxTextArea.setEditable(false);

        Theme theme;
        try {
            theme = Theme.load(getClass().getClassLoader().getResourceAsStream("themes/dark.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        theme.apply(rSyntaxTextArea);

        RTextScrollPane rTextScrollPane = new RTextScrollPane(rSyntaxTextArea);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setFocusable(false);

        URL url = getClass().getClassLoader().getResource("images/trash.png");
        ImageIcon trashIcon = new ImageIcon(Objects.requireNonNull(url));
        JButton clearButton = new JButton(trashIcon);
        clearButton.setToolTipText("Löschen");
        clearButton.addActionListener(e -> {
            if(model != null) {
                model.clear();
            }
        });

        toolBar.add(clearButton);

        add(toolBar, BorderLayout.NORTH);
        add(rTextScrollPane, BorderLayout.CENTER);
    }

    /**
     * Einfügen von Text in die Konsole.
     * @param text Der einufügende Text
     */
    @Override
    public void onInsert(String text) {
        if(rSyntaxTextArea != null) {
            Document doc = rSyntaxTextArea.getDocument();
            try {
                doc.insertString(doc.getLength(), text + "\n", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Löschen des Textes in der Konsole.
     */
    @Override
    public void onClear() {
        if(rSyntaxTextArea != null) {
            rSyntaxTextArea.setText("");
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getMinimumSize();
    }
}
