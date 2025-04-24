package com.coherentsolutions.springai.l5rag.controller;

import com.coherentsolutions.springai.l5rag.model.Answer;
import com.coherentsolutions.springai.l5rag.model.Question;
import com.coherentsolutions.springai.l5rag.services.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final OpenAIService openAIService;

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        return openAIService.getAnswer(question);
    }

}
