package com.example.mcp;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.modelcontextprotocol.client.McpSyncClient;

@RestController
@RequestMapping("mcppg")
public class McpPgController {

    private final ChatClient chatClient;

    public McpPgController(ChatClient.Builder builder, McpSyncClient mcpClient, ChatMemory chatMemory) {
        List<ToolCallback> callbacks = mcpClient.listTools()
            .tools()
            .stream()
            .map(tool -> new SyncMcpToolCallback(mcpClient, tool))
            .map(ToolCallback.class::cast)
            .toList();
        this.chatClient = builder.defaultToolCallbacks(callbacks)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
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
            .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
            .call()
            .content();
    }

}
