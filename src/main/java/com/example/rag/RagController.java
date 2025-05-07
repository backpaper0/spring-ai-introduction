package com.example.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rag")
public class RagController {

    private final ChatClient chatClient;

    public RagController(ChatClient.Builder builder, VectorStore vectorStore) {

        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .topK(1)
            .build();

        QueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder().build();

        Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
            .documentRetriever(documentRetriever)
            .queryAugmenter(queryAugmenter)
            .build();

        this.chatClient = builder.defaultAdvisors(ragAdvisor).build();

    }

    /**
     * RAGを試します。
     * @param query ユーザープロンプト
     * @return 生成されたテキスト
     */
    @PostMapping
    public String generate(@RequestParam String query) {
        return chatClient.prompt().user(query).call().content();
    }

}
