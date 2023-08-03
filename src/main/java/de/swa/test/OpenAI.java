package de.swa.test;

import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class OpenAI {

    private static String key = "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";

    public static void main(String[] args) {
        // API-KEY: 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
        OpenAiService service = new OpenAiService(key, Duration.ofSeconds(60));

        //t1(service);
        asyncTest(service);
        //service.listModels().forEach(System.out::println);

        /*CompletionRequest request = CompletionRequest.builder()
                .build();

         */

        /*GraphCode gc = GraphCodeIO.read(new File("graphcodes/4k-wallpaper-15-ULTRA-HD-Collections-_rockefellercallsitaday.jpg.gc"));

        List<ChatMessage> messageList = new ArrayList<>();
        messageList.add(new ChatMessage("user",
                "I will provide you with a vector, " +
                        "which is comprised of words, as well as a set" +
                        "of relationships between those words."));
        messageList.add(new ChatMessage("user", "The vector of words is as follows: " +
                String.join(", ", gc.getDictionary()) + "."));
        messageList.add(new ChatMessage("assistant", "The set of relationships is as follows: " +
                Explainer.explain(new ExplainableGraphCode(gc), 3, 2)));
        messageList.add(new ChatMessage("user", "These words and relationships have been extracted from an image." +
                "Please make up an explanation of these words."));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(messageList)
                .model("gpt-3.5-turbo")
                .maxTokens(256)
                .topP(0.5)
                .frequencyPenalty(0.0)
                .presencePenalty(0.22)
                .build();

        ChatCompletionResult chatCompletionResult = service.createChatCompletion(chatCompletionRequest);

        //CompletionResult chatResult = service.createCompletion(chatRequest);
        System.out.println(chatCompletionResult.getChoices().get(0).getMessage().getContent());*/

        /*List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", "Write a detailed report about " +
                "the models GPT-3.5 and GPT-4, which shall include details about access to these models, " +
                "application interfaces and their documentation, as well as pricing."));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model("gpt-4")
                .build();

        ChatCompletionResult chatCompletionResult = service.createChatCompletion(chatCompletionRequest);
        System.out.println(chatCompletionResult.getChoices().get(0).getMessage().getContent());*/

        /*CreateImageRequest imageRequest = CreateImageRequest.builder()
                //.prompt("Steampunk submarine exploring a coral reef, surrounded by exotic sea creatures and vibrant coral, detailed, surreal, steampunk style")
                .prompt("a white siamese cat")
                .n(1)
                .size("1024x1024")
                .responseFormat("url")
                .build();

        ImageResult imageResult = service.createImage(imageRequest);
        System.out.println(imageResult.getData().get(0).getUrl());*/

        //System.out.println(imageRequest);

        //ImageResult imageResult = service.createImage(imageRequest);
        //System.out.println(imageResult.getData().get(0).getUrl());
    }

    static void t1(OpenAiService service) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", "Write a detailed report about " +
                "the models GPT-3.5 and GPT-4, which shall include details about access to these models, " +
                "application interfaces and their documentation, as well as pricing."));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model("gpt-3.5-turbo-16k")
                .build();

        ChatCompletionResult chatCompletionResult = service.createChatCompletion(chatCompletionRequest);
        System.out.println(chatCompletionResult.getChoices().get(0).getMessage().getContent());
    }

    static void asyncTest(OpenAiService service) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", "Write a detailed report about " +
                "the models GPT-3.5 and GPT-4, which shall include details about access to these models, " +
                "application interfaces and their documentation, as well as pricing."));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model("gpt-3.5-turbo-16k")
                .build();

        final ChatCompletionResult[] chatCompletionResult = new ChatCompletionResult[1];

        new Thread(() -> {
            chatCompletionResult[0] = service.createChatCompletion(chatCompletionRequest);

            System.out.println(chatCompletionResult[0].getChoices().get(0).getMessage().getContent());
        }).start();

        System.out.println("Call Irgendwas");
    }
}
