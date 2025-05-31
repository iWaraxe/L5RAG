# Spring AI 1.0.0 Migration Guide

This guide details all the changes needed to migrate from Spring AI 1.0.0-M7 to 1.0.0 for each branch.

## ✅ Completed: POM Updates

All branches have been updated with Spring AI 1.0.0 dependency.

## 🔄 Required Code Changes by Branch

### Branch 01-rag-introduction
**Status**: Basic setup - minimal changes needed
- ✅ pom.xml updated
- ✅ No code changes required (basic setup only)

### Branch 02-vector-store-setup
**Status**: Configuration only - minimal changes needed
- ✅ pom.xml updated
- ✅ application.yml already has `initialize-schema: true`
- ⚠️ Verify PgVectorStore auto-configuration works with 1.0.0

### Branch 03-document-ingestion-and-splitting
**Changes needed in `DocumentLoader.java`**:
```java
// OLD (M7)
@EventListener(ApplicationReadyEvent.class)
public void loadData() {
    // ...
}

// NEW (1.0.0) - No changes needed, but verify:
// - TokenTextSplitter still works the same way
// - Document class hasn't changed
// - VectorStore.add() method signature is the same
```

### Branch 04-add-controller-and-service
**Changes needed**:
1. Verify ChatClient API hasn't changed
2. Check PromptTemplate usage is still valid
3. Ensure model/record classes are compatible

### Branch 05-query-vector-store
**Changes needed in `OpenAIServiceRagImpl.java`**:
```java
// OLD (M7)
SearchRequest searchRequest = SearchRequest.builder()
    .query(SearchRequest.query(question)
    .withTopK(4)
    .withSimilarityThreshold(0.5))
    .build();

// NEW (1.0.0)
SearchRequest searchRequest = SearchRequest.builder()
    .query(question)
    .topK(4)
    .similarityThreshold(0.5)
    .build();
```

### Branch 06-embedding-and-prompt-clarification
**Changes needed**:
1. Update any SearchRequest usage
2. Verify admin controller vector store operations

### Branch 07-qa-service-with-questionansweradvisor
**Major changes needed in `ChatClientConfig.java`**:
```java
// OLD (M7)
@Bean
ChatClient chatClient(ChatClient.Builder builder, 
                     VectorStore vectorStore,
                     @Value("classpath:templates/rag-advisor-template.st") Resource template) {
    return builder
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
        .build();
}

// NEW (1.0.0)
@Bean
ChatClient chatClient(ChatClient.Builder builder, 
                     VectorStore vectorStore,
                     @Value("classpath:templates/rag-advisor-template.st") Resource template) {
    var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
        .searchRequest(SearchRequest.builder()
            .similarityThreshold(0.5)
            .topK(4)
            .build())
        .build();
    
    return builder
        .defaultAdvisors(qaAdvisor)
        .build();
}
```

### Branch 08-retrieval-augmentation-advance
**New features to implement**:
```java
// Use RetrievalAugmentationAdvisor
var advisor = RetrievalAugmentationAdvisor.builder()
    .documentRetriever(VectorStoreDocumentRetriever.builder()
        .vectorStore(vectorStore)
        .similarityThreshold(0.5)
        .topK(6)
        .build())
    .queryTransformers(List.of(
        RewriteQueryTransformer.builder()
            .chatClientBuilder(chatClientBuilder)
            .build()
    ))
    .build();
```

### Branch 09-vectorstore-metadata-and-filtering
**Changes needed**:
```java
// Dynamic filter expressions
.advisors(a -> a.param(
    QuestionAnswerAdvisor.FILTER_EXPRESSION, 
    "genre == 'Drama' && year >= 2020"
))
```

## 📋 Common Changes Across All Branches

### 1. SearchRequest API Changes
```java
// OLD (M7)
SearchRequest.query(question)
    .withTopK(4)
    .withSimilarityThreshold(0.5)

// NEW (1.0.0)
SearchRequest.builder()
    .query(question)
    .topK(4)
    .similarityThreshold(0.5)
    .build()
```

### 2. QuestionAnswerAdvisor Builder Pattern
```java
// OLD (M7)
new QuestionAnswerAdvisor(vectorStore)

// NEW (1.0.0)
QuestionAnswerAdvisor.builder(vectorStore)
    .searchRequest(searchRequest)
    .promptTemplate(template) // optional
    .build()
```

### 3. Vector Store Configuration
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

### 4. PgVectorStore Builder (if manually configured)
```java
// NEW (1.0.0)
PgVectorStore.builder(jdbcTemplate, embeddingModel)
    .dimensions(1536)
    .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
    .indexType(PgVectorStore.PgIndexType.HNSW)
    .initializeSchema(true)
    .build();
```

## 🔧 Migration Script

A migration script is needed for each branch to:
1. Update SearchRequest usage
2. Convert QuestionAnswerAdvisor to builder pattern
3. Add any missing configuration
4. Update imports if needed

## ⚠️ Breaking Changes

1. **SearchRequest**: No longer uses static factory methods
2. **QuestionAnswerAdvisor**: Must use builder pattern
3. **Schema Initialization**: Must explicitly opt-in
4. **Filter Expressions**: New dynamic parameter support

## 🧪 Testing After Migration

For each branch:
1. Run `./mvnw clean test`
2. Start the application: `./mvnw spring-boot:run`
3. Test all endpoints
4. Verify vector store operations
5. Check embedding and retrieval functionality

## 📚 References

- [Spring AI 1.0.0 Release Notes](https://github.com/spring-projects/spring-ai/releases/tag/v1.0.0)
- [Migration Guide](https://docs.spring.io/spring-ai/reference/api/migration-guide.html)
- [RAG Documentation](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html)