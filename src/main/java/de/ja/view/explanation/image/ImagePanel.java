package de.ja.view.explanation.image;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import de.swa.gc.GraphCode;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ImagePanel extends JPanel implements ActionListener {

    private GraphCode graphCode;

    private String prompt;

    private static String key = "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";

    private JSpinner nSpinner;

    private JComboBox<String> sizeComboBox;

    private final JTextArea promptArea;

    private final JLabel picLabel;

    private List<ChatMessage> messages = new ArrayList<>();

    public ImagePanel() {
        MigLayout imagePanelMigLayout = new MigLayout("" , "[fill, grow]", "10[fill,15%][10%][][fill,75%]");
        setLayout(imagePanelMigLayout);

        promptArea = new JTextArea();
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        JScrollPane promptSP = new JScrollPane(promptArea);
        promptSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        promptSP.setBorder(new TitledBorder("Prompt"));
        add(promptSP, "cell 0 0, growx, height ::15%, aligny top");

        UIManager.put("TaskPane.animate", Boolean.FALSE);
        UIManager.put("TaskPane.titleOver", new Color(200, 200, 200));
        //UIManager.put("TaskPane.specialTitleBackground", new Color(23, 162, 162));
        UIManager.put("TaskPane.titleForeground", new Color(187, 187, 187));
        UIManager.put("TaskPane.titleBackgroundGradientStart", new Color(85, 88, 89));
        UIManager.put("TaskPane.titleBackgroundGradientEnd", new Color(85, 88, 89));
        UIManager.put("TaskPane.background", new Color(76, 80, 82));
        UIManager.put("TaskPane.borderColor", new Color(94, 96, 96));

        JXTaskPane advancedOptions = new JXTaskPane();
        advancedOptions.setCollapsed(true);
        advancedOptions.setTitle("Advanced Options");
        add(advancedOptions, "cell 0 1, growx, aligny top");

        MigLayout advancedOptionsMigLayout = new MigLayout("", "0[]5[]10[]5[]0", "0[]0");
        advancedOptions.setLayout(advancedOptionsMigLayout);

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

        JButton generateImageButton = new JButton("Generate Image(s)");
        generateImageButton.addActionListener(this);
        add(generateImageButton, "cell 0 2, width ::150px, aligny top");

        picLabel = new JLabel();
        picLabel.setBorder(new TitledBorder("Image"));
        picLabel.setHorizontalAlignment(SwingConstants.CENTER);
        picLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        add(picLabel, "cell 0 3, growx, aligny top");
    }

    public void setGraphCode(GraphCode graphCode) {
        this.graphCode = graphCode;

        if(graphCode != null) {
            prompt = setUpPrompt(graphCode);
            promptArea.setText(prompt);
        } else {
            promptArea.setText(null);
        }
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    private String setUpPrompt(GraphCode graphCode) {

        // Alle Paare die über eine 1-Beziehung verfügen.
        String s = graphCode.formattedTerms(1);

        messages = new ArrayList<>();

        messages.add(new ChatMessage(
                "system",
                "You are an assistant, who is able to generate cohesive textual explanations based on a json string.")
        );
        /*messages.add(new ChatMessage(
                "assistant",
                "The given JSON string represents a dictionary and a matrix. The dictionary " +
                        "contains the feature vocabulary terms. The matrix is a square two dimensional " +
                        "matrix where each element represents the relationship between two words. The " +
                        "value in the matrix indicates the strength of the relationship between " +
                        "the corresponding words."));*/
        messages.add(new ChatMessage(
                "assistant",
                "The given JSON string represents a dictionary. The dictionary contains the feature" +
                        "vocabulary terms. Additionally some of these terms are connected through a relationship." +
                        "These relationships will be noted as <t> - <t1,...,tn>, where <t> is a feature vocabulary term."));
        messages.add(new ChatMessage(
                "assistant",
                "Using these terms, we can create a coherent explanation that accurately " +
                        "describes the terms and its relations.\n" +
                        "\n" +
                        "An example could be: The image shows water, the sky, and clouds. " +
                        "We can imagine a beautiful scene with clouds floating in the sky above."));
        messages.add(new ChatMessage(
                "user",
                graphCode.reducedString() + ". Only respect these terms " + s + " and ignore all others. " +
                        "Do not mention the dictionary and its terms. Only generate a text containing " +
                        "the terms like in the example above."));
        messages.add(new ChatMessage(
                "assistant",
                "Based on the provided JSON string, here is a cohesive text " +
                        "containing the terms from the dictionary:"));
        return messages.stream().map(ChatMessage::getContent).collect(Collectors.joining("\n"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenAiService service = new OpenAiService(key, Duration.ofSeconds(60));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model("gpt-3.5-turbo-16k")
                .maxTokens(75)
                .build();

        final ChatCompletionResult[] chatCompletionResult = new ChatCompletionResult[1];

        final ImageResult[] imageResult = new ImageResult[1];

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Thread t = new Thread(() -> {
            chatCompletionResult[0] = service.createChatCompletion(chatCompletionRequest);
            String chatResponse = chatCompletionResult[0].getChoices().get(0).getMessage().getContent();
            System.out.println(chatResponse);

            CreateImageRequest imageRequest = CreateImageRequest.builder().
                    prompt(chatResponse)
                    .n((Integer) nSpinner.getValue())
                    .size(String.valueOf(sizeComboBox.getSelectedItem()))
                    .responseFormat("url")
                    .build();

            imageResult[0] = service.createImage(imageRequest);

            try {
                URL imageUrl = new URL(imageResult[0].getData().get(0).getUrl());
                picLabel.setIcon(new ImageIcon(imageUrl));
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        });
        executorService.execute(t);
        executorService.shutdown();
    }
}
