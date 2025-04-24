package com.coherentsolutions.springai.l5rag.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DocumentLoader {

    private final VectorStore vectorStore;

    @Value("classpath:/movies500.csv")
    private Resource csvFile;

    @PostConstruct
    public void loadCsvUsingSimpleApproach() {
        if (!vectorStore.similaritySearch("test").isEmpty()) {
            log.info("Vector store already contains data — skipping re-ingestion.");
            return;
        }

        log.info("Loading document from file: {}", csvFile.getFilename());

        TikaDocumentReader reader = new TikaDocumentReader(csvFile);
        List<Document> docs = reader.get();

        log.info("Loaded {} document(s) from CSV file", docs.size());

        TextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocs = splitter.apply(docs);

        log.info("Split document into {} chunks", splitDocs.size());

        vectorStore.add(splitDocs);

        log.info("Chunks added to vector store");
    }
}