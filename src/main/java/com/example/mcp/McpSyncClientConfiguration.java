package com.example.mcp;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.client.McpSyncClient;
import org.springframework.ai.mcp.client.stdio.ServerParameters;
import org.springframework.ai.mcp.client.stdio.StdioClientTransport;
import org.springframework.ai.mcp.spec.McpTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpSyncClientConfiguration {

    @Bean(destroyMethod = "closeGracefully")
    McpSyncClient mcpSyncClient() {
        ServerParameters params = ServerParameters.builder("npx")
            .args("-y", "@modelcontextprotocol/server-postgres", "postgres://myuser:secret@localhost:5432/mydatabase")
            .build();
        McpTransport transport = new StdioClientTransport(params);
        McpSyncClient mcpClient = McpClient.using(transport).sync();
        mcpClient.initialize();
        return mcpClient;
    }

}
