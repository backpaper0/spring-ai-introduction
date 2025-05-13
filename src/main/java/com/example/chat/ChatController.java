package com.example.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
    }

    /**
     * 会話を行います。
     * @param query ユーザープロンプト
     * @param conversationId 会話のID
     * @return 生成されたテキスト
     */
    @PostMapping("{conversationId}")
    public String post(@RequestParam String query, @PathVariable String conversationId) {
        return chatClient.prompt()
            .user(query)
            .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
            .call()
            .content();
    }

}
