package com.example.chat;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class MessageRowMapper implements RowMapper<Message> {

    @Override
    @Nullable
    public Message mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        String content = rs.getString("content");
        MessageType messageType = MessageType.fromValue(rs.getString("message_type"));
        switch (messageType) {
            case ASSISTANT:
                return new AssistantMessage(content);
            case USER:
                return new UserMessage(content);
            default:
                return null;
        }
    }

}
