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

@RequiredArgsConstructor
@Service
public class OpenAIServiceRagImpl implements OpenAIService {
    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    @Value("classpath:/templates/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    @Override
    public Answer getAnswer(Question question) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().query(question.question()).topK(5).build()
        );

        List<String> contentList = documents.stream().map(Document::getText).toList();

        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Prompt prompt = promptTemplate.create(Map.of(
                "question", question.question(),
                "documents", String.join("\n", contentList)
        ));

        ChatResponse response = chatModel.call(prompt);

        return new Answer(response.getResult().getOutput().getText());
    }
}
