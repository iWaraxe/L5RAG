package com.coherentsolutions.springai.l5rag.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DocumentLoader {

    private final VectorStore vectorStore;

    @Value("classpath:/movies500.csv")
    private Resource csvFile;

    @PostConstruct
    public void loadCsvAsDocuments() throws IOException {
        log.info("Reading CSV with Tika: {}", csvFile.getFilename());

        TikaDocumentReader reader = new TikaDocumentReader(csvFile);
        List<Document> rawDocs = reader.get();

        // Get full text from single doc (Tika loads all content as one)
        String csvText = rawDocs.get(0).getText();

        List<Document> documents = new ArrayList<>();

        // Skip header and parse rows
        BufferedReader br = new BufferedReader(new StringReader(csvText));
        String headerLine = br.readLine(); // skip header

        String row;
        while ((row = br.readLine()) != null) {
            String[] cols = row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // CSV-safe

            if (cols.length < 3) continue; // safety check

            String title = cols[0];
            String overview = cols[1];
            String genre = cols[2];

            Document doc = new Document(overview, Map.of(
                    "title", title,
                    "genre", genre
            ));

            documents.add(doc);
        }

        log.info("Parsed {} rows from CSV", documents.size());

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        vectorStore.add(chunks);
        log.info("Added {} chunks to vector store", chunks.size());
    }
}