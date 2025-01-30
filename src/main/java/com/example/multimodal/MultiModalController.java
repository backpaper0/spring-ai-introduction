package com.example.multimodal;

import java.net.URL;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("multimodal")
public class MultiModalController {

    private final ChatClient chatClient;

    public MultiModalController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * テキストと画像のマルチモーダルを試します。
     * @param query ユーザープロンプト
     * @param image 入力する画像のURL
     * @return 生成されたテキスト
     */
    @PostMapping
    public String generateMultimodal(@RequestParam String query, @RequestParam URL image) {
        return chatClient.prompt()
            .user(prompt -> prompt.text(query).media(MimeType.valueOf("image/jpeg"), image))
            .call()
            .content();
    }

}
