# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Educational Context

This project accompanies **Lecture 5: Retrieval-Augmented Generation (RAG) in Spring Applications** of the **Spring AI Mastery: Building Intelligent Applications with OpenAI** course. The course is designed for Spring Boot developers to master the integration of OpenAI's AI services using the Spring AI module over eight instructor-led lectures.

### Course Progression to Lecture 5
- **Lecture 1**: Introduction to Spring AI and OpenAI Integration
- **Lecture 2**: OpenAI API Basics in Spring Boot
- **Lecture 3**: Prompt Engineering and Output Parsing
- **Lecture 4**: Building Chatbots with Spring Boot
- **Lecture 5**: RAG in Spring Applications (THIS PROJECT)
- *Future*: Real-time voice agents and advanced topics

### Lecture 5 Learning Objectives
Students will learn to:
1. Understand the limitations of out-of-the-box AI models (knowledge cutoffs)
2. Implement RAG to augment AI with external knowledge
3. Use Spring AI's document readers, vector stores, and embeddings
4. Build a complete document Q&A system
5. Master the "open-book" pattern for AI applications

## Project Overview

A Spring Boot 3.4.4 application demonstrating Retrieval-Augmented Generation (RAG) patterns. Currently using Spring AI 1.0.0-M7, with migration to 1.0.0 in progress.

### Migration Status: M7 → 1.0.0
**Key Changes Required:**
1. **Advisor API Updates**: QuestionAnswerAdvisor has new builder pattern and configuration options
2. **Vector Store Configuration**: New batching strategy and schema initialization requirements
3. **Document Processing**: Enhanced ETL pipeline with new readers and transformers
4. **Filter Expressions**: Improved metadata filtering with dynamic expressions

## Build and Run Commands

```bash
# Build the application
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=L5RagApplicationTests

# Package as JAR
./mvnw clean package

# Skip tests during build
./mvnw clean install -DskipTests
```

## Architecture Overview

### Service Layer Pattern
The application demonstrates multiple RAG implementation approaches:
- `OpenAIService` interface with implementations:
  - `OpenAIServiceLlmImpl`: Direct LLM without context (baseline)
  - `OpenAIServiceRagImpl`: Manual RAG with vector retrieval
  - *To Add*: `OpenAIServiceAdvisorImpl`: Spring AI 1.0.0 Advisor pattern

### RAG Implementation Approaches
1. **Direct LLM** (`/ask-llm`): Shows limitations without context
2. **Manual RAG** (`/ask-rag`): Custom vector retrieval + prompt augmentation
3. **QuestionAnswerAdvisor** (`/ask-advisor`): Spring AI's simplified RAG
4. **Combined** (`/ask-combined`): Compare approaches side-by-side

### Why These Different Approaches?
- **Direct LLM**: Establishes baseline - shows knowledge gaps
- **Manual RAG**: Full control over retrieval and prompt construction
- **Advisor Pattern**: Production-ready with less boilerplate
- **Combined**: Educational - demonstrates differences

### Data Flow (ETL Pipeline)
1. **Extract**: Load movie data from `movies500.csv` via `DocumentLoader`
2. **Transform**: Chunk documents (800 tokens, 200 overlap) using `TokenTextSplitter`
3. **Load**: Embed with OpenAI and store in PostgreSQL/PGVector
4. **Retrieve**: Semantic similarity search on user queries
5. **Augment**: Inject relevant context into prompts
6. **Generate**: Produce context-aware responses

## Spring AI 1.0.0 Key Concepts

### Advisor API (New in 1.0.0)
```java
// QuestionAnswerAdvisor - Simplified RAG
var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
    .searchRequest(SearchRequest.builder()
        .similarityThreshold(0.8)
        .topK(6)
        .build())
    .promptTemplate(customTemplate)  // New: custom templates
    .build();

// RetrievalAugmentationAdvisor - Advanced RAG
var raAdvisor = RetrievalAugmentationAdvisor.builder()
    .queryTransformers(...)  // Query rewriting, compression
    .documentRetriever(...)  // Custom retrieval strategies
    .queryAugmenter(...)     // Context injection
    .build();
```

### Vector Store Enhancements
```java
// New builder pattern for PgVectorStore
PgVectorStore.builder(jdbcTemplate, embeddingModel)
    .dimensions(1536)
    .distanceType(COSINE_DISTANCE)
    .indexType(HNSW)  // Performance optimization
    .initializeSchema(true)  // Must opt-in now
    .maxDocumentBatchSize(10000)  // New batching
    .build();
```

### Document Processing (ETL)
```java
// New modular ETL pipeline
vectorStore.write(
    tokenTextSplitter.split(
        pdfReader.read()
    )
);
```

### Metadata Filtering
```java
// Dynamic filter expressions
.advisors(a -> a.param(
    QuestionAnswerAdvisor.FILTER_EXPRESSION, 
    "genre == 'Drama' && year >= 2020"
))
```

## Key Configuration Requirements

### Database Setup
- PostgreSQL 14+ with PGVector extension
- Database: `rag`
- Extensions: `vector`, `hstore`, `uuid-ossp`
- Auto-initialized if `spring.ai.vectorstore.pgvector.initialize-schema=true`

### Environment Variables
```bash
# Required
SPRING_AI_OPENAI_API_KEY=your-openai-api-key

# Optional (defaults shown)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rag
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

### Spring AI 1.0.0 Configuration
```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        initialize-schema: true  # Required in 1.0.0
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536
        max-document-batch-size: 10000
```

## Template System

StringTemplate (.st) files for prompt engineering:
- `prompt-template.st`: Basic LLM prompt
- `rag-prompt-template.st`: Manual RAG context injection
- `rag-advisor-template.st`: QuestionAnswerAdvisor user prompt
- `rag-advisor-system.st`: System-level instructions

### Why StringTemplate?
- Separation of concerns: prompts as resources
- Easy iteration without recompilation
- Support for complex template logic
- Reusable across different services

## Development Guidelines

### When Implementing RAG:
1. **Start Simple**: Use QuestionAnswerAdvisor for quick prototypes
2. **Measure Quality**: Compare with/without RAG using `/ask-combined`
3. **Tune Parameters**: Adjust similarity threshold and topK
4. **Monitor Costs**: Track embedding and completion tokens

### When Modifying Vector Behavior:
1. **Chunking Strategy**: Balance between context and precision
2. **Similarity Metrics**: COSINE for normalized, EUCLIDEAN for absolute
3. **Index Types**: HNSW for production, IVFFlat for experimentation
4. **Batch Sizes**: Larger for bulk loads, smaller for real-time

### When Adding Features:
1. Follow the existing controller/service/model pattern
2. Create comparative endpoints (with/without feature)
3. Document the "why" in code comments
4. Add integration tests for vector operations

## Educational Focus Areas

### Core Questions This Project Answers:
1. **Why RAG?** LLMs have knowledge cutoffs and lack domain-specific information
2. **Why Vector Stores?** Semantic search finds meaning, not just keywords
3. **Why Embeddings?** Convert text to mathematical representations for similarity
4. **Why Chunking?** Balance context window limits with information density
5. **Why Multiple Approaches?** Different use cases require different trade-offs

### Learning Path:
1. **Baseline Understanding**: Try `/ask-llm` with movie questions
2. **See RAG Impact**: Compare with `/ask-rag` responses
3. **Explore Advisors**: Use `/ask-advisor` for production patterns
4. **Combine & Compare**: Use `/ask-combined` to see differences
5. **Experiment**: Modify templates, thresholds, and chunking

### Common Pitfalls to Highlight:
- Not initializing schema in Spring AI 1.0.0
- Using wrong embedding dimensions
- Forgetting to chunk large documents
- Not handling empty retrieval results
- Ignoring token limits in prompts

## Migration Checklist (M7 → 1.0.0)

- [ ] Update Spring AI dependency to 1.0.0
- [ ] Add spring-ai-advisors-vector-store dependency
- [ ] Update QuestionAnswerAdvisor to builder pattern
- [ ] Add schema initialization configuration
- [ ] Implement new batching strategy if needed
- [ ] Update metadata filtering to new syntax
- [ ] Test all endpoints with new version
- [ ] Update documentation with 1.0.0 patterns

## Testing Strategy

### Unit Tests:
- Service layer RAG logic
- Document chunking behavior
- Template rendering

### Integration Tests:
- Vector store operations
- End-to-end RAG flow
- Advisor configurations

### Manual Testing:
- Question quality comparison
- Performance benchmarks
- Error handling scenarios

## Performance Considerations

### Vector Operations:
- HNSW index builds slower but queries faster
- Batch document loading for efficiency
- Monitor embedding API rate limits

### Prompt Optimization:
- Keep context focused and relevant
- Use topK=4-6 for most use cases
- Implement result caching where appropriate

## Future Enhancements

### For Educational Value:
1. Add query transformation examples
2. Implement document post-processing
3. Create visual RAG flow diagrams
4. Add performance metrics endpoints
5. Build interactive tutorial mode

### For Production Readiness:
1. Implement result caching
2. Add document version management
3. Create admin UI for vector management
4. Implement security filters
5. Add observability/monitoring

## References

- [Spring AI 1.0.0 RAG Documentation](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html)
- [Spring AI Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)
- [Spring AI ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)
- [PGVector Documentation](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html)