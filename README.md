# Spring AI RAG Tutorial - Lecture 5: Retrieval-Augmented Generation

> 🎓 **Part of Spring AI Mastery Course**: Building Intelligent Applications with OpenAI

## 📚 About This Project

This is an educational project accompanying **Lecture 5** of the Spring AI Mastery course. It demonstrates how to build a production-ready Retrieval-Augmented Generation (RAG) system using Spring Boot and Spring AI.

### 🎯 Learning Objectives

By working through this project, you will:
- **Understand** why RAG is essential for overcoming LLM limitations
- **Build** a complete document Q&A system from scratch
- **Master** vector stores, embeddings, and semantic search
- **Compare** different RAG implementation approaches
- **Apply** best practices for production RAG systems

## 🌳 Branch-Based Learning Structure

This project uses a **progressive branch structure** where each branch builds upon the previous one. Start from the beginning and work your way up!

### Branch Progression

| Branch | Topic | What You'll Learn |
|--------|-------|-------------------|
| `01-rag-introduction` | **RAG Foundations** | • Project setup with Spring AI<br>• Understanding RAG concepts<br>• Basic configuration |
| `02-vector-store-setup` | **Vector Store Setup** | • PostgreSQL with PGVector<br>• Vector store configuration<br>• Embedding preparation |
| `03-document-ingestion-and-splitting` | **ETL Pipeline** | • Document loading strategies<br>• Text chunking with TokenTextSplitter<br>• Duplicate prevention |
| `04-add-controller-and-service` | **Core Architecture** | • Service layer pattern<br>• REST endpoints<br>• Prompt templates |
| `05-query-vector-store` | **RAG Implementation** | • Similarity search<br>• Context injection<br>• Comparing LLM vs RAG responses |
| `06-embedding-and-prompt-clarification` | **Production Features** | • Admin controls<br>• Vector store management<br>• Prompt optimization |
| `07-qa-service-with-questionansweradvisor` | **Spring AI Advisors** | • QuestionAnswerAdvisor pattern<br>• Simplified RAG flow<br>• ChatClient configuration |
| `08-retrieval-augmentation-advance` 🚧 | **Advanced RAG** | • Query transformation<br>• Document post-processing<br>• Multi-stage retrieval |
| `09-vectorstore-metadata-and-filtering` 🚧 | **Metadata & Filtering** | • Dynamic filtering<br>• Metadata strategies<br>• Advanced search patterns |

> 🚧 = Branches under development for Spring AI 1.0.0

## 🚀 Getting Started

### Prerequisites

1. **Java 21** or higher
2. **PostgreSQL 14+** with PGVector extension
3. **OpenAI API Key**
4. **Maven 3.8+**

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd L5RAG
   ```

2. **Start from the beginning**
   ```bash
   git checkout 01-rag-introduction
   ```

3. **Set up PostgreSQL with PGVector**
   ```bash
   # Using Docker
   docker run -it --rm --name postgres \
     -p 5432:5432 \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     pgvector/pgvector
   ```

4. **Configure environment variables**
   ```bash
   export SPRING_AI_OPENAI_API_KEY=your-api-key-here
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

## 📖 How to Use This Tutorial

### 1. Follow the Branch Order

Each branch builds on the previous one. Don't skip ahead!

```bash
# See all branches
git branch -a

# Move to next branch
git checkout 02-vector-store-setup
```

### 2. Read the Documentation

Each major feature has detailed documentation:

| Document | Description |
|----------|-------------|
| [RAG Concepts](docs/01-rag-concepts.md) | Understanding RAG and its importance |
| [Vector Stores Explained](docs/02-vector-stores.md) | How vector databases enable semantic search |
| [Document Processing](docs/03-document-processing.md) | ETL pipeline and chunking strategies |
| [Implementation Patterns](docs/04-implementation-patterns.md) | Comparing different RAG approaches |
| [Spring AI Advisors](docs/05-advisors.md) | Using Spring AI's built-in RAG patterns |
| [Production Best Practices](docs/06-production.md) | Performance, security, and monitoring |

### 3. Try the Exercises

Each branch includes hands-on exercises in the `exercises/` directory:

```bash
exercises/
├── 01-basic-rag/          # Simple RAG implementation
├── 02-custom-splitter/    # Build your own text splitter
├── 03-metadata-filtering/ # Advanced search with filters
└── 04-performance-tuning/ # Optimize your RAG system
```

## 🏗️ Architecture Overview

### System Components

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐
│   Client    │────▶│  Controller  │────▶│   Service    │
└─────────────┘     └──────────────┘     └──────────────┘
                                                  │
                                                  ▼
                    ┌──────────────────────────────────┐
                    │          Spring AI               │
                    ├──────────────┬───────────────────┤
                    │ Embedding    │  Chat Model       │
                    │   Model      │  (OpenAI)         │
                    └──────────────┴───────────────────┘
                            │              ▲
                            ▼              │
                    ┌─────────────┐        │
                    │Vector Store │────────┘
                    │ (PGVector)  │
                    └─────────────┘
```

### Data Flow

1. **Document Ingestion**: Load → Chunk → Embed → Store
2. **Query Processing**: Question → Embed → Search → Retrieve
3. **Answer Generation**: Context + Question → LLM → Response

## 🔧 Key Technologies

- **Spring Boot 3.4.4** - Application framework
- **Spring AI 1.0.0** - AI integration (migration in progress)
- **PostgreSQL + PGVector** - Vector database
- **OpenAI API** - Embeddings and completions
- **StringTemplate** - Prompt templating

## 📊 API Endpoints

### Query Endpoints

| Endpoint | Method | Description | Purpose |
|----------|--------|-------------|---------|
| `/ask-llm` | POST | Direct LLM query | Baseline without RAG |
| `/ask-rag` | POST | Manual RAG implementation | Full control approach |
| `/ask-advisor` | POST | Spring AI Advisor | Production-ready pattern |
| `/ask-combined` | POST | Compare all approaches | Educational comparison |

### Admin Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/admin/reset-vector-store` | POST | Clear and reload vector store |
| `/admin/stats` | GET | Vector store statistics |

## 🧪 Testing the Application

### Example Queries

1. **Test without context (LLM only)**
   ```bash
   curl -X POST http://localhost:8080/ask-llm \
     -H "Content-Type: application/json" \
     -d '{"question": "What is the plot of Inception?"}'
   ```

2. **Test with RAG**
   ```bash
   curl -X POST http://localhost:8080/ask-rag \
     -H "Content-Type: application/json" \
     -d '{"question": "What is the plot of Inception?"}'
   ```

3. **Compare approaches**
   ```bash
   curl -X POST http://localhost:8080/ask-combined \
     -H "Content-Type: application/json" \
     -d '{"question": "Who directed The Dark Knight?"}'
   ```

## 📈 Performance Considerations

### Vector Store Optimization

- **HNSW Index**: Better query performance, slower builds
- **Chunk Size**: 800 tokens (balance context vs precision)
- **Overlap**: 200 tokens (maintain context continuity)
- **Batch Size**: 10,000 documents (configurable)

### Cost Management

- Monitor embedding API calls
- Cache frequently accessed embeddings
- Optimize chunk sizes to reduce tokens
- Use similarity thresholds wisely

## 🤝 Contributing

This is an educational project. To add new topics:

1. Create a new branch following the naming pattern
2. Build upon the previous branch
3. Add comprehensive documentation
4. Include practical exercises
5. Update this README

## 📚 Additional Resources

### Spring AI Documentation
- [Spring AI RAG Guide](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html)
- [Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)
- [ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)

### Course Materials
- [Course Overview](docs/course-overview.md)
- [Lecture Slides](resources/slides/)
- [Video Tutorials](https://course-link-here)

## ❓ Troubleshooting

### Common Issues

1. **Vector store not initializing**
   ```yaml
   spring.ai.vectorstore.pgvector.initialize-schema: true
   ```

2. **Embedding dimension mismatch**
   - Ensure consistency between model and vector store
   - Default: 1536 for OpenAI text-embedding-3-small

3. **Out of memory with large documents**
   - Adjust chunk size and batch size
   - Increase JVM heap size

## 📝 License

This educational project is part of the Spring AI Mastery course.

---

## 🎯 Next Steps

1. **Complete all branches** in order
2. **Try the exercises** to reinforce learning
3. **Build your own RAG** system using these patterns
4. **Share your learnings** with the community

**Happy Learning! 🚀**