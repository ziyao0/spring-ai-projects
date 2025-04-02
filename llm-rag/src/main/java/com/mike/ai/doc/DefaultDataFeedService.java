package com.mike.ai.doc;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ziyao
 * @link <a href="https://github.com/ziyao0">ziyao for github</a>
 */
@Service
@RequiredArgsConstructor
public class DefaultDataFeedService implements DataFeedService, InitializingBean {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    /**
     * 初始化向量数据库
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        List<Document> documents = ClassPathDocumentLoader.loadDocuments("rag"
                , new TextDocumentParser());
        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
                "\\n\\d+\\.",
                "\n",
                500,
                100,
                new DocumentByCharacterSplitter(500, 100)
        );
        List<TextSegment> textSegments = splitter.splitAll(documents);

        List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();

        embeddingStore.addAll(embeddings, textSegments);

    }
}
