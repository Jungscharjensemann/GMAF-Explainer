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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private ModelType modelType;

    public GeneratedTextsPanel() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Generated Text(s)"));

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

    public void addTabsFromResult(ChatCompletionResult chatCompletionResult, ModelType type) {
        if(textsTabbedPane != null) {
            for(int i = 0; i < chatCompletionResult.getChoices().size(); i++) {
                // Generierter Text.
                String content = chatCompletionResult.getChoices().get(i).getMessage().getContent();
                // Tokens bestimmen.
                this.modelType = type;
                EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
                Encoding enc = registry.getEncodingForModel(type);
                System.out.println("Using encoding for model: " + modelType.name());
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
                        final String[] content = {String.valueOf(text)};
                        // Wenn Tokens hervorheben ausgewählt ist.
                        if(highlightTokens.isSelected()) {
                            // Anfangsposition.
                            final int[] pos = {0};
                            if(tokensMap != null && !tokensMap.isEmpty()) {
                                List<Integer> tokens = tokensMap.get(i);
                                if(tokens != null && !tokens.isEmpty()) {
                                    ExecutorService service = Executors.newSingleThreadExecutor();
                                    Thread highlight = new Thread(() -> {
                                        // Index für Farbe eines Tokens.
                                        for(int tok = 0, tokC = 0; tok < tokens.size(); tok++, tokC = ++tokC % 5) {
                                            String decoded = enc.decode(List.of(tokens.get(tok)));
                                            if(content[0].length() >= decoded.length()) {
                                                String sub = content[0].substring(0, decoded.length());
                                                if(content[0].startsWith(sub)) {
                                                    try {
                                                        ta.getHighlighter().addHighlight(pos[0], pos[0] += sub.length(), highlightPainters[tokC]);
                                                    } catch (BadLocationException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                                    content[0] = content[0].substring(decoded.length());
                                                }
                                            }
                                        }
                                    });
                                    service.execute(highlight);
                                    service.shutdown();
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
