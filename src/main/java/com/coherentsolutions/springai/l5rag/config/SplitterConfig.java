package com.coherentsolutions.springai.l5rag.config;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the text splitting strategy for document chunking.
 */
@Configuration
public class SplitterConfig {

    /**
     * Defines a TokenTextSplitter with a chunk size of 800 tokens and 200-token overlap.
     * This helps retain context across splits while ensuring chunks fit LLM limits.
     */
    @Bean
    public TextSplitter textSplitter() {
        return new TokenTextSplitter(
                800,   // chunkSize: Target size in tokens
                200,   // minChunkSizeChars: Minimum length of a chunk (in characters)
                100,   // minChunkLengthToEmbed: Minimum size required for embedding
                1000,  // maxNumChunks: Total number of chunks to allow per document
                false  // keepSeparator: Typically false unless your delimiter is meaningful
        );
    }
}