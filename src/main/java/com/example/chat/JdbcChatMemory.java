package com.example.chat;

import java.util.Collections;
import java.util.List;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * 間に合わせの実装です。
 * 
 * 下記のPRで開発されています。
 * 
 * https://github.com/spring-projects/spring-ai/pull/1528
 */
@Component
public class JdbcChatMemory implements ChatMemory {

    private final JdbcClient jdbc;

    public JdbcChatMemory(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            jdbc.sql("""
                    insert into chat_memory
                    ( conversation_id, message_type, content )
                    values
                    ( :conversation_id, :message_type, :content )
                    """)
                .param("conversation_id", conversationId)
                .param("message_type", message.getMessageType().getValue())
                .param("content", message.getText())
                .update();
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messages = jdbc.sql("""
                select * from chat_memory
                where conversation_id = :conversation_id
                order by id desc
                offset 0 limit :limit
                """)
            .param("conversation_id", conversationId)
            .param("limit", lastN)
            .query(new MessageRowMapper())
            .list();
        Collections.reverse(messages);
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        jdbc.sql("delete from chat_memory where conversation_id = :conversation_id")
            .param("conversation_id", conversationId)
            .update();
    }

}
