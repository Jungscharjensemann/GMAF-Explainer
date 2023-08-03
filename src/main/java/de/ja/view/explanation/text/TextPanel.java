package de.ja.view.explanation.text;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.swa.gc.GraphCode;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TextPanel extends JPanel implements ActionListener {

    private GraphCode graphCode;

    private String prompt;

    private static String key = "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";

    private JSpinner temperatureSpinner, topPSpinner, nSpinner,
            maxTokensSpinner, presencePenaltySpinner, frequencyPenaltySpinner;

    private final JTextArea promptArea;
    private final JTextArea textResponseArea;

    private List<ChatMessage> messages = new ArrayList<>();

    public TextPanel() {
        MigLayout imagePanelMigLayout = new MigLayout("" , "[fill, grow]", "10[fill,15%][12.5%][][fill,72.5%]");
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

        JLabel temperatureLabel = new JLabel("Temperature:");
        temperatureLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel temperatureSpinnerModel = new SpinnerNumberModel(0, 0, 2, 0.1);
        temperatureSpinner = new JSpinner();
        temperatureSpinner.setModel(temperatureSpinnerModel);

        JLabel topPLabel = new JLabel("Top P:");
        topPLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        topPLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel topPSpinnerModel = new SpinnerNumberModel(0, 0, 1, 0.01);
        topPSpinner = new JSpinner();
        topPSpinner.setModel(topPSpinnerModel);

        JLabel nLabel = new JLabel("N:");
        nLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        nLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel nSpinnerModel = new SpinnerNumberModel(1, 1, 2, 1);
        nSpinner = new JSpinner();
        nSpinner.setModel(nSpinnerModel);

        JLabel maxTokensLabel = new JLabel("Max Tokens:");
        maxTokensLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        maxTokensLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel maxTokensSpinnerModel = new SpinnerNumberModel(0, 0, 8192, 1);
        maxTokensSpinner = new JSpinner();
        maxTokensSpinner.setModel(maxTokensSpinnerModel);

        JLabel presencePenaltyLabel = new JLabel("Presence Penalty:");
        presencePenaltyLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        presencePenaltyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel presencePenaltySpinnerModel = new SpinnerNumberModel(0, -2, 2, 0.1);
        presencePenaltySpinner = new JSpinner();
        presencePenaltySpinner.setModel(presencePenaltySpinnerModel);

        JLabel frequencyPenaltyLabel = new JLabel("Frequency Penalty:");
        frequencyPenaltyLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        frequencyPenaltyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        SpinnerNumberModel frequencyPenaltySpinnerModel = new SpinnerNumberModel(0, -2, 2, 0.1);
        frequencyPenaltySpinner = new JSpinner();
        frequencyPenaltySpinner.setModel(frequencyPenaltySpinnerModel);

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
        advancedOptions.add(frequencyPenaltySpinner);

        JButton generateChatCompletions = new JButton("Generate Chat-Completion(s)");
        generateChatCompletions.addActionListener(this);
        add(generateChatCompletions, "cell 0 2, width ::190px, aligny top");

        textResponseArea = new JTextArea();
        textResponseArea.setLineWrap(true);
        textResponseArea.setWrapStyleWord(true);

        JScrollPane textResponseAreaSP = new JScrollPane(textResponseArea);
        textResponseAreaSP.setBorder(new TitledBorder("Text"));

        add(textResponseAreaSP, "cell 0 3, growx, aligny top");
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

    private String setUpPrompt(GraphCode graphCode) {
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
                /*.temperature((Double) temperatureSpinner.getValue())
                .topP((Double) topPSpinner.getValue())
                .n((Integer) nSpinner.getValue())
                .maxTokens((Integer) maxTokensSpinner.getValue())
                .presencePenalty((Double) presencePenaltySpinner.getValue())
                .frequencyPenalty((Double) frequencyPenaltySpinner.getValue())*/
                .build();

        final ChatCompletionResult[] chatCompletionResult = new ChatCompletionResult[1];

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Thread t = new Thread(() -> {
            chatCompletionResult[0] = service.createChatCompletion(chatCompletionRequest);

            textResponseArea.setText(chatCompletionResult[0].getChoices().get(0).getMessage().getContent());
        });
        executor.execute(t);
        executor.shutdown();
    }
}
