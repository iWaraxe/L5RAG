package com.coherentsolutions.springai.l5rag.services;

import com.coherentsolutions.springai.l5rag.model.Answer;
import com.coherentsolutions.springai.l5rag.model.Question;

public interface OpenAIService {
    Answer getAnswer(Question question);
}

