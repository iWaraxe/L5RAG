package com.coherentsolutions.springai.l5rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ChatClientConfig {

    private final VectorStore vectorStore;

    @Value("classpath:templates/rag-advisor-template.st")
    private Resource advisorTemplate;

    @Value("classpath:templates/rag-advisor-system.st")
    private Resource systemTemplate;

    public ChatClientConfig(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor() {
        /* ---------- 1. Build the SearchRequest (top-K etc.) ---------- */
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(10)                      // number of chunks
                .similarityThreshold(0.30)   // optional
                .build();

        /* ---------- 2. Build custom PromptTemplate for Spring AI 1.0.0 ---------- */
        PromptTemplate customPromptTemplate = new PromptTemplate(advisorTemplate);

        /* ---------- 3. Build the advisor with new 1.0.0 API ---------- */
        return QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(searchRequest)
                .promptTemplate(customPromptTemplate)  // New in 1.0.0
                .build();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, QuestionAnswerAdvisor questionAnswerAdvisor) {
        return ChatClient.builder(model)
                // the advisor injects DOCUMENTS into the user-role message
                .defaultAdvisors(questionAnswerAdvisor) // add more advisors here (.andThen(...))
                .build();
    }

    @Bean
    public ChatClient metadataFilteringChatClient(OpenAiChatModel model, VectorStore vectorStore) {
        // Demonstration of dynamic metadata filtering in Spring AI 1.0.0
        QuestionAnswerAdvisor filteringAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(5)
                        .similarityThreshold(0.4)
                        .build())
                .promptTemplate(new PromptTemplate(advisorTemplate))
                .build();
        
        return ChatClient.builder(model)
                .defaultAdvisors(filteringAdvisor)
                .build();
    }
}

