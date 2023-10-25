package de.ja.view.explanation.text.textinfo;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

/**
 * Diese Klasse stellt ein Panel für
 * die generierten Texte dar.
 */
public class GeneratedTextsPanel extends JPanel implements ActionListener, ChangeListener {

    // InfoPanel.
    private final JPanel infoOptionsPanel;

    // Anzahl an Tokens.
    private final JLabel tokenCountLabel;

    // Hervorheben der Tokens.
    private final JCheckBox highlightTokens;

    // TabbedPane für die generierten Texte.
    private final JTabbedPane textsTabbedPane;

    // Tokens.
    private final HashMap<Integer, List<Integer>> tokensMap;

    // Painters für Hervorheben der Tokens.
    private final Highlighter.HighlightPainter[] highlightPainters;

    // Textmodell generativer KI.
    private final ModelType modelType;

    public GeneratedTextsPanel(ModelType modelType) {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Generated Text(s)"));

        this.modelType = modelType;
        // Tokens.
        tokensMap = new HashMap<>();
        // Farben.
        Color[] colors = new Color[]{
                new Color(204, 191, 238),
                new Color(190, 237, 198),
                new Color(246, 217, 171),
                new Color(244, 174, 177),
                new Color(164, 220, 243)
        };

        highlightPainters = new Highlighter.HighlightPainter[] {
                new DefaultHighlighter.DefaultHighlightPainter(colors[0]),
                new DefaultHighlighter.DefaultHighlightPainter(colors[1]),
                new DefaultHighlighter.DefaultHighlightPainter(colors[2]),
                new DefaultHighlighter.DefaultHighlightPainter(colors[3]),
                new DefaultHighlighter.DefaultHighlightPainter(colors[4]),
        };

        infoOptionsPanel = new JPanel();
        infoOptionsPanel.setVisible(false);

        tokenCountLabel = new JLabel("Token Count: ");

        highlightTokens = new JCheckBox("Highlight Tokens");
        highlightTokens.addActionListener(this);

        textsTabbedPane = new JTabbedPane();
        textsTabbedPane.addChangeListener(this);
        // Layout für InfoPanel.
        MigLayout infoLayout = new MigLayout("", "5[]10[]10[]5[]0", "0[]0");
        infoOptionsPanel.setLayout(infoLayout);

        infoOptionsPanel.add(tokenCountLabel);
        infoOptionsPanel.add(highlightTokens);

        add(infoOptionsPanel, BorderLayout.NORTH);
        add(textsTabbedPane, BorderLayout.CENTER);
    }

    public void addTabsFromResult(ChatCompletionResult chatCompletionResult) {
        if(textsTabbedPane != null) {
            for(int i = 0; i < chatCompletionResult.getChoices().size(); i++) {
                // Generierter Text.
                String content = chatCompletionResult.getChoices().get(i).getMessage().getContent();
                // Tokens bestimmen.
                EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
                Encoding enc = registry.getEncodingForModel(modelType);
                List<Integer> encoded = enc.encode(content);
                // Tokens in Relation zum Index des Tabs speichern.
                if(tokensMap != null) {
                    tokensMap.put(i, encoded);
                }
                // Textfeld für generierten Text.
                JTextArea textResponseArea = new JTextArea();
                textResponseArea.setLineWrap(true);
                textResponseArea.setWrapStyleWord(true);
                textResponseArea.setText(content);
                textResponseArea.setEditable(false);
                JScrollPane textResponseAreaSP = new JScrollPane(textResponseArea);
                // Tab mit Textfeld hinzufügen.
                textsTabbedPane.addTab(String.format("Text-%s", i + 1), textResponseAreaSP);
            }
            if(textsTabbedPane.getTabCount() > 0) {
                infoOptionsPanel.setVisible(true);
                highlightTokens.setSelected(false);
            }
        }
    }

    /**
     * Diese Methode setzt
     * die Tabs und Tokens zurück.
     */
    public void reset() {
        if(textsTabbedPane != null) {
            textsTabbedPane.removeAll();
            infoOptionsPanel.setVisible(false);
            if(tokensMap != null) {
                tokensMap.clear();
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if(textsTabbedPane != null) {
            int count = 0;
            if(tokensMap != null && !tokensMap.isEmpty()) {
                List<Integer> tokens = tokensMap.get(textsTabbedPane.getSelectedIndex());
                if(tokens != null && !tokens.isEmpty()) {
                    count = tokens.size();
                }
            }
            if(tokenCountLabel != null) {
                tokenCountLabel.setText(String.format("Token Count: %s", count));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(textsTabbedPane != null) {
            // Tokens hervorheben.
            int tabCount = textsTabbedPane.getTabCount();
            EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
            // Spezifische Kodierung für Modelltyp.
            Encoding enc = registry.getEncodingForModel(modelType);
            // Alle Tabs durchlaufen.
            for(int i = 0; i < tabCount; i++) {
                Component comp = textsTabbedPane.getComponentAt(i);
                if(comp instanceof JScrollPane) {
                    JViewport viewport = ((JScrollPane) comp).getViewport();
                    comp = viewport.getView();
                    if(comp instanceof JTextArea) {
                        JTextArea ta = (JTextArea) comp;
                        String text = ta.getText();
                        String content = String.valueOf(text);
                        // Wenn Tokens hervorheben ausgewählt ist.
                        if(highlightTokens.isSelected()) {
                            // Anfangsposition.
                            int pos = 0;
                            int contentLen = content.length();
                            if(tokensMap != null && !tokensMap.isEmpty()) {
                                List<Integer> tokens = tokensMap.get(i);
                                if(tokens != null && !tokens.isEmpty()) {
                                    // Index für Farbe eines Tokens.
                                    int tokC = 0;
                                    for (Integer token : tokens) {
                                        // Token dekodieren.
                                        String decoded = enc.decode(List.of(token));
                                        if (content.length() >= decoded.length()) {
                                            // Prüfen, ob Anfang nächstem Token entspricht.
                                            String sub = content.substring(0, decoded.length());
                                            if (content.startsWith(sub)) {
                                                try {
                                                    // Token hervorheben.
                                                    ta.getHighlighter().addHighlight(pos, pos + sub.length(), highlightPainters[tokC]);
                                                    pos += sub.length();
                                                    content = content.substring(decoded.length());
                                                } catch (BadLocationException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                            }
                                        }
                                        // Zyklisch zurückspringen.
                                        tokC = (tokC + 1) % 5;
                                    }
                                    if(pos == contentLen) {
                                        System.out.println("Successfully reached end of text!");
                                    }
                                }
                            }
                            ta.setForeground(Color.BLACK);
                        } else {
                            ta.getHighlighter().removeAllHighlights();
                            ta.setForeground(null);
                        }
                    }
                }
            }
        }
    }
}
