package com.coherentsolutions.springai.l5rag.controller;

import com.coherentsolutions.springai.l5rag.model.Answer;
import com.coherentsolutions.springai.l5rag.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdvisorQuestionController {

    private final ChatClient chatClient;

    @PostMapping("/ask-advisor")
    public Answer ask(@RequestBody Question q) {
        String content = chatClient.prompt()
                .user(q.question())
                .call()
                .content();
        return new Answer(content);
    }
}