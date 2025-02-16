package com.example.function;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("function")
public class FunctionCallingController {

    private final ChatClient chatClient;

    public FunctionCallingController(ChatClient.Builder builder) {
        // function callingで使用する関数をbean名で登録
        this.chatClient = builder.defaultTools("resolveGitHubAccount").build();
    }

    /**
     * function callingを試します。
     * @param query ユーザープロンプト
     * @return 生成されたテキスト
     */
    @PostMapping
    public String post(@RequestParam String query) {
        return chatClient.prompt().user(query).call().content();
    }

}
