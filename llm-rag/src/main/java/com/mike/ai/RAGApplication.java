package com.mike.ai;

import com.mike.ai.chat.AssistantChat;
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

    @Override
    public void run(String... args) throws Exception {
        String chat = assistantChat.chat("你是谁？");
        System.out.println(chat);
    }
}
