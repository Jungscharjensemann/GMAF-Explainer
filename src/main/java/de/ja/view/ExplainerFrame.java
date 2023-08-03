package de.ja.view;

import de.ja.model.console.ExplainerConsoleModel;
import de.ja.view.console.ExplainerConsole;
import de.ja.view.editor.EditorGraphCode;
import de.ja.view.explanation.ExplanationPanel;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static org.jdesktop.swingx.MultiSplitLayout.*;

public class ExplainerFrame extends JFrame {

    private EditorGraphCode editorGraphCode;

    private ExplanationPanel explanationPanel;

    public ExplainerFrame() {
        initFrame();
        configureFrame();
        initComponents();
    }

    private void initFrame() {
        // relative Höhe des Fensters (60%).
        double heightPercentage = 0.65;
        // Seitenverhältnis
        double aspectRatio = 16.0 / 10.0;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int) (dim.height * heightPercentage);
        int width = (int) (height * aspectRatio);

        // Parameter des Haupt-Frames konfigurieren.
        setTitle("Explainer - Bachelor [Jens Nathan Andreß, 9763180]");
        setBounds((dim.width - width) / 2, (dim.height - height) / 2, width, height);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void configureFrame() {

    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(); {
            mainPanel.setLayout(new BorderLayout());

            mainPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    mainPanel.revalidate();
                }
            });

            JXMultiSplitPane multiSplitPane = new JXMultiSplitPane(); {

                Split main = new Split(); {
                    main.setRowLayout(false);

                    Split top = new Split();
                    top.setWeight(0.8);

                    Leaf top_left = new Leaf("top.left");
                    Leaf top_right = new Leaf("top.right");

                    top.setChildren(top_left, new Divider(), top_right);

                    Leaf bottom = new Leaf("bottom");
                    bottom.setWeight(0.2);

                    main.setChildren(top, new Divider(), bottom);
                }

                MultiSplitLayout layout = new MultiSplitLayout(main);
                multiSplitPane.setLayout(layout);

                editorGraphCode = new EditorGraphCode(this);

                explanationPanel = new ExplanationPanel();

                ExplainerConsoleModel explainerConsoleModel = new ExplainerConsoleModel();
                ExplainerConsole explainerConsole = new ExplainerConsole(explainerConsoleModel);

                multiSplitPane.add(editorGraphCode, "top.left");
                multiSplitPane.add(explanationPanel, "top.right");
                multiSplitPane.add(explainerConsole, "bottom");
                multiSplitPane.setContinuousLayout(true);
            }
            mainPanel.add(multiSplitPane, BorderLayout.CENTER);
        }
        getContentPane().add(mainPanel);
    }

    public EditorGraphCode getEditorGraphCode() {
        return editorGraphCode;
    }

    public ExplanationPanel getExplanationPanel() {
        return explanationPanel;
    }
}
