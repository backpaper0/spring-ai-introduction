package com.example.structure;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("structure")
public class StructuredOutputController {

    private final ChatClient chatClient;

    public StructuredOutputController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * 構造化された出力を試します。
     * @param query ユーザープロンプト
     * @return 生成されたJSONから変換されたオブジェクト
     */
    @PostMapping
    public MainChatacters post(@RequestParam String query) {
        return chatClient.prompt().user(query).call().entity(MainChatacters.class);
    }

}
