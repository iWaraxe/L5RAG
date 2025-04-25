package com.coherentsolutions.springai.l5rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptBuilder;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.context.postprocessor.DefaultContextPostProcessor;
import org.springframework.ai.rag.context.postprocessor.ContextPostProcessor;
import org.springframework.ai.rag.retrieval.source.VectorStoreDocumentRetriever;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AdvancedRagConfig {

    @Bean
    QuestionAnswerAdvisor ragAdvisor(VectorStore store,
                                     TextSplitter splitter) {

        /* 1.  Document retriever */
        VectorStore retriever =
                VectorStoreDocumentRetriever.builder()
                        .vectorStore(store)
                        .topK(12)
                        .similarityThreshold(0.4f)
                        // .filterExpression(() -> "genres = 'Animation'")
                        .build();

        /* 2.  Post-processor (dedup + 2 000-token cap) */
        ContextPostProcessor post =
                new DefaultContextPostProcessor(splitter, 2_000);

        /* 3.  Prompt builder */
        PromptBuilder promptBuilder = (query, docs) -> List.of(
                ChatClient.system("""
                        You are a movie-knowledge assistant.
                        If the answer is not in DOCUMENTS say "I don't know."
                        """),
                ChatClient.user("""
                        QUESTION:
                        %s
                        
                        DOCUMENTS:
                        %s
                        """.formatted(query, String.join("\n----\n", docs)))
        );

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .contextPostProcessor(post)
                .promptBuilder(promptBuilder)
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