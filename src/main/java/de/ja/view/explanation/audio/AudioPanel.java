package de.ja.view.explanation.audio;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.audio.CreateSpeechRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import de.ja.model.audio.SpeechResult;
import de.ja.view.ExplainerFrame;
import de.ja.view.explanation.audio.player.AudioPlayerPanel;
import de.swa.gc.GraphCode;
import net.miginfocom.swing.MigLayout;
import okhttp3.ResponseBody;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Diese Klasse stellt die Benutzerschnittstelle
 * zum Erstellen einer auditiven Erklärung von
 * Graph Codes dar.
 */
public class AudioPanel extends JPanel implements ActionListener {

    // API-Key.
    private static String key;

    private final JComboBox<String> modelComboBox;

    private final JComboBox<String> voiceComboBox;

    private final JComboBox<String> formatComboBox;

    private final JSpinner speedSpinner;

    // Textfeld für die generierte Prompt.
    private final JTextArea promptArea;
    private final JButton generateAudioButton;

    // Nachrichten, die die Prompt darstellen.
    private List<ChatMessage> messages = new ArrayList<>();

    private final AudioPlayerPanel audioPlayerPanel;

    // Referenz.
    private final ExplainerFrame reference;

    public AudioPanel(ExplainerFrame reference) {
        this.reference = reference;
        key = System.getenv("OpenAI-Key");
        // Layout definieren.
        MigLayout imagePanelMigLayout = new MigLayout("" , "[fill, grow]", "10[10%][][][fill,30%][60%]"); //1. 10%
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

        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        modelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        modelComboBox = new JComboBox<>();
        modelComboBox.addItem("tts-1");
        modelComboBox.addItem("tts-1-hd");

        JLabel voiceLabel = new JLabel("Voice:");
        voiceLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        voiceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        voiceComboBox = new JComboBox<>();
        voiceComboBox.addItem("alloy");
        voiceComboBox.addItem("echo");
        voiceComboBox.addItem("fable");
        voiceComboBox.addItem("onyx");
        voiceComboBox.addItem("nova");
        voiceComboBox.addItem("shimmer");

        JLabel formatLabel = new JLabel("Format:");
        formatLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        formatLabel.setHorizontalAlignment(SwingConstants.CENTER);

        formatComboBox = new JComboBox<>();
        formatComboBox.addItem("mp3");
        formatComboBox.addItem("opus");
        formatComboBox.addItem("aac");
        formatComboBox.addItem("flac");

        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel nSpinnerNumberModel = new SpinnerNumberModel(1, 0.25, 4.0, 0.01);
        speedSpinner = new JSpinner();
        speedSpinner.setModel(nSpinnerNumberModel);

        advancedOptions.add(modelLabel);
        advancedOptions.add(modelComboBox);
        advancedOptions.add(voiceLabel);
        advancedOptions.add(voiceComboBox);
        advancedOptions.add(formatLabel);
        advancedOptions.add(formatComboBox);
        advancedOptions.add(speedLabel);
        advancedOptions.add(speedSpinner);

        // Knopf zum Generieren von Bildern.
        generateAudioButton = new JButton("Generate Audio");
        generateAudioButton.addActionListener(this);
        add(generateAudioButton, "cell 0 1, width ::150px, aligny top");

        audioPlayerPanel = new AudioPlayerPanel();
        add(audioPlayerPanel,"cell 0 2, growx, aligny top");
    }

    /**
     * Graph Code verarbeiten
     * @param graphCode Ausgewählter Graph Code.
     */
    public void setGraphCode(GraphCode graphCode) {
        if(graphCode != null) {
            // Prompt vorbereiten.
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
        // Alle Paare die über eine 1-Beziehung verfügen.
        String s = graphCode.getFormattedTerms();
        // Textnachrichten für die Prompt.
        messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "You are an assistant, who is able to generate cohesive textual explanations based on a collection of words."));
        messages.add(new ChatMessage(
                ChatMessageRole.ASSISTANT.value(),
                "The collection of words represents a dictionary. The dictionary contains so-called feature " +
                        "vocabulary terms. Additionally some of these terms are connected through a relationship. " +
                        "These relationships will be noted as <i_t> - <i_t1,...,i_tn>, where i_t denotes the index of a feature " +
                        "vocabulary term in the given collection."));
        messages.add(new ChatMessage(
                "assistant",
                "Using these terms, we can create a coherent explanation that accurately " +
                        "describes the terms and its relations.\n" +
                        "\n" +
                        "An example could be: The image shows water, the sky, and clouds. " +
                        "We can imagine a scene with clouds floating in the sky above."));
        messages.add(new ChatMessage(
                "user",
                "The collections of words is as follows: " + graphCode.listTerms() + ". Only respect these terms and its relations: " + s + ", and ignore all others. " +
                        "Do not create an explanation regarding the dictionary. Only generate a text containing " +
                        "the terms of the dictionary like in the example above."));
        messages.add(new ChatMessage(
                "assistant",
                "Based on the dictionary, here is a cohesive text " +
                        "containing the terms from the dictionary:"));
        // Nachrichten zusammenfügen.
        return messages.stream().map(ChatMessage::getContent).collect(Collectors.joining("\n"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        audioPlayerPanel.resetSpeechResult();
        // Anbindung zur Schnittstelle.
        OpenAiService service = new OpenAiService(key, Duration.ofSeconds(60));
        if(key.isEmpty()) {
            reference.getExplainerConsoleModel().insertText("OpenAI-Key is missing, abort process. Must be set in launch-config: OpenAI-Key=...");
            return;
        }
        // Prozess erstellen.
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Thread t = new Thread(() -> {
            // Textanfrage initialisieren und parametrisieren.
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model("gpt-4")
                    .maxTokens(256)
                    .build();

            try {
                // Cursor auf Warten setzen.
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                // Knopf deaktivieren.
                generateAudioButton.setEnabled(false);
                // Info in der Konsole ausgeben.
                reference.getExplainerConsoleModel()
                        .insertText("Generating an auditive explanation!");
                // Textanfrage an Endpunkt senden.
                ChatCompletionResult chatCompletionResult = service.createChatCompletion(chatCompletionRequest);
                // Ergebnis der Anfrage.
                String chatResponse = chatCompletionResult.getChoices().get(0).getMessage().getContent();
                // Bildanfrage initialisieren und parametrisieren.
                CreateSpeechRequest speechRequest = CreateSpeechRequest.builder()
                        .input(chatResponse)
                        .model((String) Objects.requireNonNull(modelComboBox.getSelectedItem()))
                        .voice((String) Objects.requireNonNull(voiceComboBox.getSelectedItem()))
                        .responseFormat((String) formatComboBox.getSelectedItem())
                        .speed((Double) speedSpinner.getValue())
                        .build();
                ResponseBody speechResponseBody = service.createSpeech(speechRequest);
                SpeechResult speechResult = SpeechResult.builder()
                        .audioType(speechResponseBody.contentType())
                        .bytes(speechResponseBody.bytes())
                        .build();
                audioPlayerPanel.setSpeechResult(speechResult);
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
                generateAudioButton.setEnabled(true);
            }
        });
        // Prozess ausführen und beenden.
        executorService.execute(t);
        executorService.shutdown();
    }
}
