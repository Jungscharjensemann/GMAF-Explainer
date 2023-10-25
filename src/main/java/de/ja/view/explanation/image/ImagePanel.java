package de.ja.view.explanation.image;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import de.ja.view.ExplainerFrame;
import de.swa.gc.GraphCode;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FilenameUtils;
import org.jdesktop.swingx.JXTaskPane;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Diese Klasse stellt die Benutzerschnittstelle
 * zum Erstellen von Bildern bzw. visuellen Erklärungen von
 * Graph Codes dar.
 */
public class ImagePanel extends JPanel implements ActionListener {

    // API-Key.
    private static String key;

    // Anzahl zu generierender Bilder.
    private final JSpinner nSpinner;

    // Größe der zu generierenden Bilder.
    private final JComboBox<String> sizeComboBox;

    // Textfeld für die generierte Prompt.
    private final JTextArea promptArea;

    // TabbedPane für alle generierten Bilder.
    private final JTabbedPane imagesTabbedPane;

    // Nachrichten, die die Prompt darstellen.
    private List<ChatMessage> messages = new ArrayList<>();

    // Referenz.
    private final ExplainerFrame reference;

    public ImagePanel(ExplainerFrame reference) {
        this.reference = reference;
        key = System.getenv("OpenAI-Key");
        // Layout definieren.
        MigLayout imagePanelMigLayout = new MigLayout("" , "[fill, grow]", "10[10%][][fill,60%][fill,30%]");
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

        JLabel nLabel = new JLabel("N:");
        nLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        nLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel nSpinnerNumberModel = new SpinnerNumberModel(1, 1, 10, 1);
        nSpinner = new JSpinner();
        nSpinner.setModel(nSpinnerNumberModel);

        JLabel sizeLabel = new JLabel("Size:");
        sizeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        sizeComboBox = new JComboBox<>();
        sizeComboBox.addItem("256x256");
        sizeComboBox.addItem("512x512");
        //sizeComboBox.addItem("1024x1024");

        advancedOptions.add(nLabel);
        advancedOptions.add(nSpinner);
        advancedOptions.add(sizeLabel);
        advancedOptions.add(sizeComboBox);

        // Knopf zum Generieren von Bildern.
        JButton generateImageButton = new JButton("Generate Image(s)");
        generateImageButton.addActionListener(this);
        add(generateImageButton, "cell 0 1, width ::150px, aligny top");

        imagesTabbedPane = new JTabbedPane();
        imagesTabbedPane.setBorder(new TitledBorder("Generated Image(s)"));
        add(imagesTabbedPane,"cell 0 2, growx, aligny top");
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
        String s = graphCode.getFormattedTerms2();
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
        imagesTabbedPane.removeAll();
        // Anbindung zur Schnittstelle.
        OpenAiService service = new OpenAiService(key, Duration.ofSeconds(60));
        // Prozess erstellen.
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Thread t = new Thread(() -> {
            // Textanfrage initialisieren und parametrisieren.
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model("gpt-3.5-turbo-16k")
                    // 75 - 250?
                    .maxTokens(200)
                    .build();

            try {
                // Cursor auf Warten setzen.
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                // Info in der Konsole ausgeben.
                RuleBasedNumberFormat numberFormat = new RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.SPELLOUT);
                reference.getExplainerConsoleModel()
                        .insertText(String.format("Generating %s visual explanation%s!",
                                numberFormat.format(nSpinner.getValue()),
                                (int) nSpinner.getValue() > 1 ? "s" : ""));
                // Textanfrage an Endpunkt senden.
                ChatCompletionResult chatCompletionResult = service.createChatCompletion(chatCompletionRequest);
                // Ergebnis der Anfrage.
                String chatResponse = chatCompletionResult.getChoices().get(0).getMessage().getContent();
                // Bildanfrage initialisieren und parametrisieren.
                CreateImageRequest imageRequest = CreateImageRequest.builder().
                        // Limit (Zeichenlänge): 1000
                        prompt(chatResponse)
                        .n((Integer) nSpinner.getValue())
                        .size(String.valueOf(sizeComboBox.getSelectedItem()))
                        .responseFormat("url")
                        .build();
                // Bildanfrage an Endpunkt senden.
                ImageResult imageResult = service.createImage(imageRequest);
                // Alle Ergebnisse verarbeiten und anzeigen.
                for(int i = 0; i < imageResult.getData().size(); i++) {
                    URL imageUrl = new URL(imageResult.getData().get(i).getUrl());
                    ImageIcon icon = new ImageIcon(imageUrl);
                    // Label zum Darstellen eines generierten Bildes.
                    JLabel imageLabel = new JLabel();
                    imageLabel.setIcon(icon);
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    // Tab mit Bild hinzufügen.
                    imagesTabbedPane.addTab(String.format("Image-%s", i + 1), imageLabel);
                    // Bild in Ordner speichern.
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
                    String nameFormat = FilenameUtils.getName(imageUrl.getPath());
                    String fileName = String.format("explanations/%s-%s", timeStamp, nameFormat);
                    File saveImg = new File(fileName);
                    ImageIO.write(ImageIO.read(imageUrl), "jpg", saveImg);
                }
            } catch (Exception ex) {
                // Fehler in Konsole ausgeben.
                reference.getExplainerConsoleModel().insertText(ex.getMessage());
            } finally {
                // Cursor auf Standard zurücksetzen.
                setCursor(Cursor.getDefaultCursor());
            }
        });
        // Prozess ausführen und beenden.
        executorService.execute(t);
        executorService.shutdown();
    }
}
