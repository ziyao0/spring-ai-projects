package com.ziyao.ai.alibaba.controllers;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author ziyao
 * @link <a href="https://github.com/ziyao0">ziyao for github</a>
 */
@RestController
public class ChatController {

    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(DEFAULT_PROMPT)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
    }

    @GetMapping("chat/{message}")
    public Flux<String> chat(@PathVariable String message, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(message).stream().content();
    }

    /**
     * ChatClient 使用自定义的 Advisor 实现功能增强.
     * eg:
     * <a href='http://127.0.0.1:18080/helloworld/advisor/chat/123?query=你好，我叫牧生，之后的会话中都带上我的名字'>你好，我叫牧生，之后的会话中都带上我的名字</a>
     * <p>
     * 你好，牧生！很高兴认识你。在接下来的对话中，我会记得带上你的名字。有什么想聊的吗？
     * <p>
     * <a href='http://127.0.0.1:18080/helloworld/advisor/chat/123?query=我叫什么名字？'>我叫什么名字？</a>
     * <p>
     * 你叫牧生呀。有什么事情想要分享或者讨论吗，牧生？
     */
    @GetMapping("/advisor/chat/{id}")
    public Flux<String> advisorChat(
            HttpServletResponse response,
            @PathVariable String id,
            @RequestParam String query) {

        response.setCharacterEncoding("UTF-8");

        return this.chatClient.prompt(query)
                .advisors(
                        a -> a
                                .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, id)
                                .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                ).stream().content();
    }
}
