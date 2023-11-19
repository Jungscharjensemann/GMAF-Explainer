package de.ja.view.explanation.image;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.theokanning.openai.OpenAiHttpException;
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
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private final JComboBox<String> modelTypeComboBox;
    private final JComboBox<String> qualityComboBox;
    private final JComboBox<String> styleComboBox;

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
        MigLayout imagePanelMigLayout = new MigLayout("" , "[fill, grow]", "10[10%][][fill,60%][fill,30%]"); //1. 10%
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
        sizeComboBox.addItem("1024x1024");
        sizeComboBox.addItem("1024x1792");
        sizeComboBox.addItem("1792x1024");

        JLabel modelTypeLabel = new JLabel("Model Type:");
        modelTypeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        modelTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        modelTypeComboBox = new JComboBox<>();
        modelTypeComboBox.addItem("dall-e-2");
        modelTypeComboBox.addItem("dall-e-3");

        JLabel qualityLabel = new JLabel("Quality:");
        qualityLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        qualityLabel.setHorizontalAlignment(SwingConstants.CENTER);

        qualityComboBox = new JComboBox<>();
        qualityComboBox.addItem("standard");
        qualityComboBox.addItem("hd");

        JLabel styleLabel = new JLabel("Style:");
        styleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        styleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        styleComboBox = new JComboBox<>();
        styleComboBox.addItem("vivid");
        styleComboBox.addItem("natural");

        advancedOptions.add(modelTypeLabel);
        advancedOptions.add(modelTypeComboBox);
        advancedOptions.add(nLabel);
        advancedOptions.add(nSpinner);
        advancedOptions.add(qualityLabel);
        advancedOptions.add(qualityComboBox);
        advancedOptions.add(styleLabel);
        advancedOptions.add(styleComboBox);
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
        imagesTabbedPane.removeAll();
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

                CreateImageRequest imageRequest = CreateImageRequest.builder()
                        .prompt(chatResponse)
                        .n((Integer) nSpinner.getValue())
                        .model((String) modelTypeComboBox.getSelectedItem())
                        .quality((String) qualityComboBox.getSelectedItem())
                        .style((String) styleComboBox.getSelectedItem())
                        .size(String.valueOf(sizeComboBox.getSelectedItem()))
                        .responseFormat("url")
                        .build();

                // Bildanfrage an Endpunkt senden.
                ImageResult imageResult = service.createImage(imageRequest);
                // Alle Ergebnisse verarbeiten und anzeigen.
                for(int i = 0; i < imageResult.getData().size(); i++) {
                    URL imageUrl = new URL(imageResult.getData().get(i).getUrl());
                    ImageIcon icon = new ImageIcon(imageUrl);
                    String imageName = String.format("Image-%s", i + 1);
                    if(icon.getIconWidth() > 256 && icon.getIconHeight() > 256) {
                        JButton external = new JButton("Open Image in external Frame...");
                        external.addActionListener(e1 -> {
                            JFrame externalFrame = new JFrame();
                            externalFrame.setTitle(imageName);
                            externalFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                            externalFrame.setLocationRelativeTo(null);

                            JLabel imageLabel = new JLabel();
                            imageLabel.setIcon(icon);
                            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);

                            JPanel panel = new JPanel();
                            panel.add(imageLabel);

                            externalFrame.add(new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
                            externalFrame.setSize(512, 512);
                            externalFrame.setVisible(true);

                            //JOptionPane.showMessageDialog(null, new JScrollPane(imageLabel), imageName, JOptionPane.PLAIN_MESSAGE, null);
                        });
                        imagesTabbedPane.addTab(String.format("Image-%s", i + 1), external);
                    } else if(icon.getIconWidth() == 256 && icon.getIconHeight() == 256) {
                        // Label zum Darstellen eines generierten Bildes.
                        JLabel imageLabel = new JLabel();
                        imageLabel.setIcon(icon);
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                        // Tab mit Bild hinzufügen.
                        imagesTabbedPane.addTab(imageName, imageLabel);
                    }
                    // Bild in Ordner speichern.
                    Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/explanations/image/"));
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
                    String nameFormat = FilenameUtils.getName(imageUrl.getPath());
                    String fileName = String.format("explanations/image/%s-%s", timeStamp, nameFormat);
                    File saveImg = new File(fileName);
                    ImageIO.write(ImageIO.read(imageUrl), "jpg", saveImg);
                }
            } catch(OpenAiHttpException openAiHttpException) {
                if(openAiHttpException.statusCode == 401) {
                    JOptionPane.showMessageDialog(null,
                            "You provided an invalid API-Key!",
                            "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    reference.getExplainerConsoleModel().insertText("You provided an invalid API-Key!");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
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
