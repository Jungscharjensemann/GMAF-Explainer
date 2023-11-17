package de.swa.test;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenAI {

    public static void main(String[] args) {
        OpenAiService service = new OpenAiService(System.getenv("OpenAI-Key"), Duration.ofSeconds(60));
        t1(service);
        asyncTest(service);
        service.listModels().forEach(System.out::println);
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
