package com.coherentsolutions.springai.l5rag.config;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.vectorstore.retrieval.source.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.context.postprocessor.DefaultContextPostProcessor;
import org.springframework.ai.chat.client.prompt.PromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AdvancedRagConfig {

    @Bean
    RetrievalAugmentationAdvisor ragAdvisor(VectorStore store, TextSplitter splitter) {
        VectorStoreDocumentRetriever retriever = new VectorStoreDocumentRetriever.Builder()
                .withVectorStore(store)
                .withTopK(12)
                .withSimilarityThreshold(0.4f)
                .build();

        ContextPostProcessor postProcessor = new DefaultContextPostProcessor.Builder()
                .withTextSplitter(splitter)
                .withTokenLimit(2000)
                .build();

        PromptBuilder promptBuilder = (query, docs) -> List.of(
                ChatClient.system("""
            You are a movie-knowledge assistant.
            If the answer is not in DOCUMENTS say "I don't know."
            """),
                ChatClient.user("""
            QUESTION: %s
            DOCUMENTS: %s
            """.formatted(query, String.join("\n----\n", docs)))
        );

        return new RetrievalAugmentationAdvisor.Builder()
                .withDocumentRetriever(retriever)
                .withContextPostProcessor(postProcessor)
                .withPromptBuilder(promptBuilder)
                .build();
    }

    @Bean
    ChatClient chatClient(OpenAiChatModel model,
                          RetrievalAugmentationAdvisor ragAdvisor) {

        return ChatClient.builder(model)
                .defaultAdvisors(ragAdvisor)
                .build();
    }
}