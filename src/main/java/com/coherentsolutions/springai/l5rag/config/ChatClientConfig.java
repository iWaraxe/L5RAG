package com.coherentsolutions.springai.l5rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class ChatClientConfig {

    private final VectorStore vectorStore;

    public ChatClientConfig(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor() throws IOException {
        /* ---------- 1. Build the SearchRequest (top-K etc.) ---------- */
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(5)                      // number of chunks
                .similarityThreshold(0.80f)   // optional
                .build();

        /* ---------- 2. Load the prompt template file as String ---------- */
        String userTextAdvise = Files.readString(
                new ClassPathResource("templates/rag-prompt-template.st")
                        .getFile().toPath());

        /* ---------- 3. Build the advisor ---------- */
        return QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(searchRequest)
                .userTextAdvise(userTextAdvise)   // << here!
                .build();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, QuestionAnswerAdvisor questionAnswerAdvisor) {

        return ChatClient.builder(model)
                .defaultAdvisors(questionAnswerAdvisor) // add more advisors here (.andThen(...))
                .build();
    }
}

