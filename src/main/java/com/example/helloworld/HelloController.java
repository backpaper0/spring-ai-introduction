package com.example.helloworld;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("hello")
public class HelloController {

    private final ChatClient chatClient;

    public HelloController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * 単純なテキスト生成です。
     * @param query ユーザープロンプト
     * @return 生成されたテキスト
     */
    @PostMapping
    public String generate(@RequestParam String query) {
        return chatClient.prompt().user(query).call().content();
    }

    /**
     * 単純なテキスト生成のServer-Sent Events版です。
     * @param query ユーザープロンプト
     * @return 生成されたテキスト
     */
    @PostMapping(path = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String query) {
        return chatClient.prompt().user(query).stream().content();
    }

    /**
     * システムプロンプトを設定したテキスト生成です。
     * @param query ユーザープロンプト
     * @return 生成されたテキスト
     */
    @PostMapping("with_system_prompt")
    public String generateWithSystemPrompt(@RequestParam String query) {
        String system = """
                あなたはJava言語のエキスパートです。Javaに関する質問へ自信満々に回答してください。
                回答するときは語尾を「じゃば」にしてください。""";
        return chatClient.prompt().system(system).user(query).call().content();
    }

}
