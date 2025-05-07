package com.example.mcp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;

@Configuration
public class McpSyncClientConfiguration {

    @Bean(destroyMethod = "closeGracefully")
    McpSyncClient mcpSyncClient() {
        ServerParameters params = ServerParameters.builder("npx")
            .args("-y", "@modelcontextprotocol/server-postgres", "postgres://myuser:secret@localhost:5432/mydatabase")
            .build();
        McpClientTransport transport = new StdioClientTransport(params);
        McpSyncClient mcpClient = McpClient.sync(transport).build();
        mcpClient.initialize();
        return mcpClient;
    }

}
