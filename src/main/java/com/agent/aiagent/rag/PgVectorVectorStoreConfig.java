package com.agent.aiagent.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

//@Configuration
public class PgVectorVectorStoreConfig {

    private static final int VECTOR_DIMENSIONS = 1536;
    private static final String SCHEMA_NAME = "public";
    private static final String VECTOR_TABLE_NAME = "vector_store";
    private static final int MAX_DOCUMENT_BATCH_SIZE = 10_000;


  //  @Bean(name = "pgVectorVectorStore")
  //  @Primary
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(VECTOR_DIMENSIONS)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName(SCHEMA_NAME)
                .vectorTableName(VECTOR_TABLE_NAME)
                .maxDocumentBatchSize(MAX_DOCUMENT_BATCH_SIZE)
                .build();
    }
}
