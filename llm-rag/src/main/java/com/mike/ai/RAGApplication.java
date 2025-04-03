package com.mike.ai;

import com.mike.ai.chat.AssistantChat;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ziyao
 * @link <a href="https://github.com/ziyao0">ziyao for github</a>
 */
@SpringBootApplication
public class RAGApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RAGApplication.class, args);
    }

    @Resource
    private AssistantChat assistantChat;
    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;
    @Resource
    private EmbeddingModel embeddingModel;

    @Override
    public void run(String... args) throws Exception {

        Embedding queryEmbedding = embeddingModel.embed("零拷贝").content();

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder().queryEmbedding(queryEmbedding)
                .minScore(0.8)
                .build();

        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(request);
        for (EmbeddingMatch<TextSegment> match : search.matches()) {

            System.out.println(match.score());
            System.out.println(match.embedded().text());
        }

        String chat = assistantChat.chat("什么是netty？");
        System.out.println(chat);
    }
}
