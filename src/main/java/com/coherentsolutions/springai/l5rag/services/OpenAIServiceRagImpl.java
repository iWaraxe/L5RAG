package com.coherentsolutions.springai.l5rag.services;

import com.coherentsolutions.springai.l5rag.model.Answer;
import com.coherentsolutions.springai.l5rag.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * OpenAIServiceRagImpl is a service class that implements a basic Retrieval-Augmented Generation (RAG) pattern.
 * <p>
 * The RAG pipeline here works as follows:
 * <ol>
 *   <li>Accepts a user's natural language question.</li>
 *   <li>Uses semantic similarity to find relevant document chunks from a vector store.</li>
 *   <li>Injects those chunks into a prompt template as context.</li>
 *   <li>Sends the context-augmented prompt to an LLM to get a grounded, accurate answer.</li>
 * </ol>
 * <p>
 * This RAG service leverages Spring AI's ChatModel, VectorStore, PromptTemplate, and SearchRequest APIs.
 */
@RequiredArgsConstructor
@Service
public class OpenAIServiceRagImpl implements OpenAIService {
    /**
     * ChatModel provides the abstraction over the LLM (e.g. OpenAI, Anthropic, etc.)
     */
    private final ChatModel chatModel;
    /**
     * VectorStore is backed by PGVector; stores document chunks as embeddings for semantic retrieval.
     */
    private final VectorStore vectorStore;

    /**
     * The template file defines how to inject documents and questions into a natural language prompt.
     */
    @Value("classpath:/templates/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    /**
     * Executes the full RAG pipeline:
     * <ul>
     *   <li>Retrieves top-k similar documents to the user question.</li>
     *   <li>Injects these documents and the question into a prompt template.</li>
     *   <li>Sends the prompt to the LLM and returns its response.</li>
     * </ul>
     *
     * @param question the user-provided question text
     * @return the AI-generated answer based on both the retrieved documents and the question
     */
    @Override
    public Answer getAnswer(Question question) {
        // Step 1: Perform a semantic search in the vector store using the user question.
        // The vector store uses the embedding of the question to find the most relevant document chunks.
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question.question()) // what the user is asking
                        .topK(5)                    // how many similar documents to retrieve
                        .build()
        );

        // Step 2: Extract the text content of each document for prompt context injection.
        // We only need the raw content of the document chunks for the prompt, not metadata.
        List<String> contentList = documents.stream()
                .map(Document::getText)
                .toList();

        // Step 3: Load and apply the prompt template, passing in both question and retrieved documents.
        // The prompt template is typically a file with placeholders for question and document context.
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Prompt prompt = promptTemplate.create(Map.of(
                "question", question.question(),
                "documents", String.join("\n", contentList) // join chunks as context
        ));

        // Step 4: Send the composed prompt to the LLM and capture the generated response.
        // The LLM receives both the user's question and the grounded context from retrieved documents.
        ChatResponse response = chatModel.call(prompt);

        // Step 5: Wrap the raw text result in our Answer DTO and return.
        return new Answer(response.getResult().getOutput().getText());
    }
}
