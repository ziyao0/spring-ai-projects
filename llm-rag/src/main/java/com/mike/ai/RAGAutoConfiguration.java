package com.mike.ai;

import com.mike.ai.chat.AssistantChat;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ziyao
 * @link <a href="https://github.com/ziyao0">ziyao for github</a>
 */
@Configuration
public class RAGAutoConfiguration {

    @Bean
    public EmbeddingModel embeddingModel() {

        return QwenEmbeddingModel.builder()
                .apiKey(System.getProperty("API_KEY"))
                .modelName("text-embedding-v2")
                .build();
    }

    @Bean
    public ChatLanguageModel chatModel() {
        return QwenChatModel.builder()
                .apiKey(System.getProperty("API_KEY"))
                .modelName("qwen-max")
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return RedisEmbeddingStore.builder()
                .dimension(384)
                .build();
    }


    @Bean
    public ContentRetriever contentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel())
                .embeddingStore(embeddingStore())
                .maxResults(2)
                .minScore(0.7)
                .build();
    }

    @Bean
    public AssistantChat assistantChat() {
        return AiServices.builder(AssistantChat.class)
                .chatLanguageModel(chatModel())
                .contentRetriever(contentRetriever())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
