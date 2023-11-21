package de.ja.view.explanation.text;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import de.ja.view.ExplainerFrame;
import de.ja.view.explanation.text.textinfo.GeneratedTextsPanel;
import de.swa.gc.GraphCode;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TextPanel extends JPanel implements ActionListener {

    // API-Key.
    private static String key;

    // Textfeld für die generierte Prompt.
    private final JTextArea promptArea;

    private final JSpinner temperatureSpinner;
    private final JSpinner topPSpinner;

    // Anzahl zu generierender Texte.
    private final JSpinner nSpinner;
    private final JSpinner maxTokensSpinner;
    private final JSpinner presencePenaltySpinner;
    private final JSpinner frequencyPenaltySpinner;

    private final JComboBox<String> modelTypeComboBox;
    private final JButton generateChatCompletions;

    // Nachrichten, die die Prompt darstellen.
    private List<ChatMessage> messages = new ArrayList<>();

    // Panel für alle generierten Texte.
    private final GeneratedTextsPanel generatedTextsPanel;

    // Referenz.
    private final ExplainerFrame reference;

    public TextPanel(ExplainerFrame reference) {
        this.reference = reference;
        key = System.getenv("OpenAI-Key");
        // Layout definieren.
        MigLayout imagePanelMigLayout = new MigLayout("" , "[fill, grow]", "10[12.5%][][fill,57.5%][fill,30%]"); //1. 12.5%
        setLayout(imagePanelMigLayout);
        // Textfeld für die Prompt initialisieren und konfigurieren.
        promptArea = new JTextArea();
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setEditable(false);
        JScrollPane promptSP = new JScrollPane(promptArea);
        promptSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        promptSP.setBorder(new TitledBorder("Generated Prompt"));
        add(promptSP, "cell 0 3, growx, height ::30%, aligny top");
        // Ästhetische Eigenschaften für erweiterte Optionen einstellen...
        UIManager.put("TaskPane.animate", Boolean.FALSE);
        UIManager.put("TaskPane.titleOver", new Color(200, 200, 200));
        UIManager.put("TaskPane.titleForeground", new Color(187, 187, 187));
        UIManager.put("TaskPane.titleBackgroundGradientStart", new Color(85, 88, 89));
        UIManager.put("TaskPane.titleBackgroundGradientEnd", new Color(85, 88, 89));
        UIManager.put("TaskPane.background", new Color(76, 80, 82));
        UIManager.put("TaskPane.borderColor", new Color(94, 96, 96));
        // Erweiterte Optionen initialisieren und konfigurieren.
        JXTaskPane advancedOptions = new JXTaskPane();
        advancedOptions.setCollapsed(true);
        advancedOptions.setTitle("Advanced Options");
        add(advancedOptions, "cell 0 0, growx, aligny top");
        // Layout für die Optionen in den erweiterten Optionen definieren.
        MigLayout advancedOptionsMigLayout = new MigLayout("", "0[]5[]10[]5[]0", "0[]0");
        advancedOptions.setLayout(advancedOptionsMigLayout);

        // Erweiterte Optionen definieren.

        JLabel temperatureLabel = new JLabel("Temperature:");
        temperatureLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);

        temperatureSpinner = new JSpinner();
        SpinnerNumberModel temperatureSpinnerModel = new SpinnerNumberModel(0, 0, 2, 0.1);
        temperatureSpinner.setModel(temperatureSpinnerModel);

        JLabel topPLabel = new JLabel("Top P:");
        topPLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        topPLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPSpinner = new JSpinner();
        SpinnerNumberModel topPSpinnerModel = new SpinnerNumberModel(0, 0, 1, 0.01);
        topPSpinner.setModel(topPSpinnerModel);

        JLabel nLabel = new JLabel("N:");
        nLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        nLabel.setHorizontalAlignment(SwingConstants.CENTER);

        nSpinner = new JSpinner();
        SpinnerNumberModel nSpinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        nSpinner.setModel(nSpinnerModel);

        JLabel maxTokensLabel = new JLabel("Max Tokens:");
        maxTokensLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        maxTokensLabel.setHorizontalAlignment(SwingConstants.CENTER);

        maxTokensSpinner = new JSpinner();
        SpinnerNumberModel maxTokensSpinnerModel = new SpinnerNumberModel(256, 0, 8192, 1);
        maxTokensSpinner.setModel(maxTokensSpinnerModel);

        JLabel presencePenaltyLabel = new JLabel("Presence Penalty:");
        presencePenaltyLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        presencePenaltyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        presencePenaltySpinner = new JSpinner();
        SpinnerNumberModel presencePenaltySpinnerModel = new SpinnerNumberModel(0, -2, 2, 0.1);
        presencePenaltySpinner.setModel(presencePenaltySpinnerModel);

        JLabel frequencyPenaltyLabel = new JLabel("Frequency Penalty:");
        frequencyPenaltyLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        frequencyPenaltyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        frequencyPenaltySpinner = new JSpinner();
        SpinnerNumberModel frequencyPenaltySpinnerModel = new SpinnerNumberModel(0, -2, 2, 0.1);
        frequencyPenaltySpinner.setModel(frequencyPenaltySpinnerModel);

        JLabel modelTypeLabel = new JLabel("Model Type:");
        modelTypeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        modelTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        modelTypeComboBox = new JComboBox<>();
        for(ModelType type : ModelType.values()) {
            modelTypeComboBox.addItem(type.getName());
        }

        advancedOptions.add(temperatureLabel);
        advancedOptions.add(temperatureSpinner);
        advancedOptions.add(topPLabel);
        advancedOptions.add(topPSpinner);
        advancedOptions.add(nLabel);
        advancedOptions.add(nSpinner, "wrap");
        advancedOptions.add(maxTokensLabel);
        advancedOptions.add(maxTokensSpinner);
        advancedOptions.add(presencePenaltyLabel);
        advancedOptions.add(presencePenaltySpinner);
        advancedOptions.add(frequencyPenaltyLabel);
        advancedOptions.add(frequencyPenaltySpinner, "wrap");
        advancedOptions.add(modelTypeLabel);
        advancedOptions.add(modelTypeComboBox, "width ::84px");

        // Knopf zum Generieren von Texten.
        generateChatCompletions = new JButton("Generate Chat-Completion(s)");
        generateChatCompletions.addActionListener(this);
        add(generateChatCompletions, "cell 0 1, width ::190px, aligny top");

        // Modell der generativen KI.
        generatedTextsPanel = new GeneratedTextsPanel();

        add(generatedTextsPanel, "cell 0 2, growx, aligny top");
    }

    /**
     * Graph Code verarbeiten
     * @param graphCode Ausgewählter Graph Code.
     */
    public void setGraphCode(GraphCode graphCode) {

        if(graphCode != null) {
            String prompt = setUpPrompt(graphCode);
            promptArea.setText(prompt);
        } else {
            promptArea.setText(null);
        }
    }

    /**
     * Prompt vorbereiten und aus Graph Code
     * generieren.
     * @param graphCode Ausgewählter Graph Code.
     * @return Generierte Prompt.
     */
    private String setUpPrompt(GraphCode graphCode) {
        String s = graphCode.getFormattedTerms();

        messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "You are an assistant, who is able to generate cohesive textual explanations based on a collection of words."));
        messages.add(new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                "The collection of words represents a dictionary. The dictionary contains so-called feature " +
                        "vocabulary terms. Additionally some of these terms are connected through a relationship. " +
                        "These relationships will be noted as <i_t> - <i_t1,...,i_tn>, where i_t denotes the index of a feature " +
                        "vocabulary term in the given collection."));
        messages.add(new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                "Using these terms, we can create a coherent explanation that accurately " +
                        "describes the terms and its relations.\n" +
                        "\n" +
                        "An example could be: The image shows water, the sky, and clouds. " +
                        "We can imagine a scene with clouds floating in the sky above."));
        messages.add(new ChatMessage(
                ChatMessageRole.USER.value(),
                "The collections of words is as follows: " + graphCode.listTerms() + ". Only respect these terms and its relations: " + s + ", and ignore all others. " +
                        "Do not create an explanation regarding the dictionary. Only generate a text containing " +
                        "the terms of the dictionary like in the example above."));
        messages.add(new ChatMessage(
                ChatMessageRole.ASSISTANT.value(),
                "Based on the dictionary, here is a cohesive text " +
                        "containing the terms from the dictionary:"));
        // Nachrichten zusammenfügen.
        return messages.stream().map(ChatMessage::getContent).collect(Collectors.joining("\n"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Tabs zurücksetzen.
        generatedTextsPanel.reset();
        // Anbindung zur Schnittstelle.
        OpenAiService service = new OpenAiService(key, Duration.ofSeconds(60));
        if(key.isEmpty()) {
            reference.getExplainerConsoleModel().insertText("OpenAI-Key is missing, abort process. Must be set in launch-config: OpenAI-Key=...");
            return;
        }
        // Prozess erstellen.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Thread t = new Thread(() -> {
            // Textanfrage initialisieren und parametrisieren.
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model((String) modelTypeComboBox.getSelectedItem())
                    .temperature((Double) temperatureSpinner.getValue())
                    .topP((Double) topPSpinner.getValue())
                    .n((Integer) nSpinner.getValue())
                    .maxTokens((Integer) maxTokensSpinner.getValue())
                    .presencePenalty((Double) presencePenaltySpinner.getValue())
                    .frequencyPenalty((Double) frequencyPenaltySpinner.getValue())
                    .build();

            try {
                // Cursor auf Warten setzen.
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                // Knopf deaktivieren.
                generateChatCompletions.setEnabled(false);
                // Info in der Konsole ausgeben.
                RuleBasedNumberFormat numberFormat = new RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.SPELLOUT);
                reference.getExplainerConsoleModel()
                        .insertText(String.format("Generating %s textual explanation%s!",
                                numberFormat.format(nSpinner.getValue()),
                                (int) nSpinner.getValue() > 1 ? "s" : ""));
                // Textanfrage an Endpunkt senden.
                ChatCompletionResult chatCompletionResult = service.createChatCompletion(chatCompletionRequest);
                // Anhand des Ergebnisses Tabs hinzufügen.
                Optional<ModelType> model = ModelType.fromName((String) modelTypeComboBox.getSelectedItem());
                ModelType modelType = ModelType.GPT_4;
                if(model.isPresent()) {
                    modelType = model.get();
                }
                generatedTextsPanel.addTabsFromResult(chatCompletionResult, modelType);
                // Texte speichern.
                for(int i = 0; i < chatCompletionResult.getChoices().size(); i++) {
                    Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/explanations/text/"));
                    String content = chatCompletionResult.getChoices().get(i).getMessage().getContent();
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
                    String fileName = String.format("explanations/text/%s-text-%s.txt", timeStamp, i + 1);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                    writer.write(content);
                    writer.close();
                }
            } catch(OpenAiHttpException openAiHttpException) {
                if(openAiHttpException.statusCode == 401) {
                    JOptionPane.showMessageDialog(null,
                            "You provided an invalid API-Key!",
                            "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    reference.getExplainerConsoleModel().insertText("You provided an invalid API-Key!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // Fehler in Konsole ausgeben.
                reference.getExplainerConsoleModel().insertText(ex.getMessage());
            } finally {
                // Cursor auf Standard zurücksetzen.
                setCursor(Cursor.getDefaultCursor());
                // Knopf reaktivieren.
                generateChatCompletions.setEnabled(true);
            }
        });
        // Prozess ausführen und beenden.
        executor.execute(t);
        executor.shutdown();
    }
}
