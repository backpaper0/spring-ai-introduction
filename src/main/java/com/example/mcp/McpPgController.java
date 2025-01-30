package com.example.mcp;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.mcp.client.McpSyncClient;
import org.springframework.ai.mcp.spring.McpFunctionCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mcppg")
public class McpPgController {

    private final ChatClient chatClient;

    public McpPgController(ChatClient.Builder builder, McpSyncClient mcpClient, ChatMemory chatMemory) {
        McpFunctionCallback[] callbacks = mcpClient.listTools()
            .tools()
            .stream()
            .map(tool -> new McpFunctionCallback(mcpClient, tool))
            .toArray(McpFunctionCallback[]::new);
        this.chatClient = builder.defaultFunctions(callbacks)
            .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
            .build();
    }

    /**
     * MCPでPostgreSQLから情報を取得して会話します。
     * @param query ユーザープロンプト
     * @param conversationId 会話のID
     * @return 生成されたテキスト
     */
    @PostMapping("{conversationId}")
    public String post(@RequestParam String query, @PathVariable String conversationId) {
        return chatClient.prompt()
            .user(query)
            .advisors(
                    advisor -> advisor.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
            .call()
            .content();
    }

}
