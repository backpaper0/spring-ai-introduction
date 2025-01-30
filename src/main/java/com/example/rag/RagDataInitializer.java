package com.example.rag;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * RAGの例のためサンプルデータを登録するクラスです。
 * 
 */
@Component
public class RagDataInitializer implements InitializingBean {

    private final VectorStore vectorStore;

    private final JdbcClient jdbc;

    private final Resource ragData;

    public RagDataInitializer(VectorStore vectorStore, JdbcClient jdbc,
            @Value("classpath:/rag_data.txt") Resource ragData) {
        this.vectorStore = vectorStore;
        this.jdbc = jdbc;
        this.ragData = ragData;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        int count = jdbc.sql("select count(*) from vector_store")
            .query(SingleColumnRowMapper.newInstance(Integer.class))
            .single();
        if (count == 0) {
            List<Document> documents = ragData.getContentAsString(StandardCharsets.UTF_8)
                .lines()
                .filter(s -> !s.isBlank())
                .map(Document::new)
                .toList();
            vectorStore.add(documents);
        }
    }

}
