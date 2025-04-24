package com.coherentsolutions.springai.l5rag.controller;

import com.coherentsolutions.springai.l5rag.model.Answer;
import com.coherentsolutions.springai.l5rag.model.CombinedAnswer;
import com.coherentsolutions.springai.l5rag.model.Question;
import com.coherentsolutions.springai.l5rag.services.OpenAIService;
import com.coherentsolutions.springai.l5rag.services.OpenAIServiceLlmImpl;
import com.coherentsolutions.springai.l5rag.services.OpenAIServiceRagImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final OpenAIServiceLlmImpl openAIServiceLlm;
    private final OpenAIServiceRagImpl openAIServiceRag;

    @PostMapping("/ask-llm")
    public Answer askQuestionLlm(@RequestBody Question question) {
        return openAIServiceLlm.getAnswer(question);
    }

    @PostMapping("/ask-rag")
    public Answer askQuestionRag(@RequestBody Question question) {
        return openAIServiceRag.getAnswer(question);
    }

    @PostMapping("/ask-combined")
    public CombinedAnswer askCombined(@RequestBody Question question) {
        Answer llm = openAIServiceLlm.getAnswer(question);
        Answer rag = openAIServiceRag.getAnswer(question);

        return new CombinedAnswer(llm.answer(), rag.answer());
    }

}
