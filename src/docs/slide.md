---
marp: true
_class: lead
---

# 【朗報】Spring AIを使えば<br/>Javaエンジニアでも生成AIと遊べるぞ

---

## 導入

* 生成AIの盛り上がりがヤバい
* 調べるとPythonの情報ばかりが出てきてヤバい
* Javaエンジニアであるオレたちの明日がヤバい
* Spring AI「やあ」
* 哀れなオレたち「オマエなら必ず来てくれると信じてたぞ！！！」
* というわけでオレたちの救世主 **Spring AI** を紹介します。

---

## 自己紹介

- うらがみ
- [GitHub](https://github.com/backpaper0?tab=repositories&q=spring&type=public&language=&sort=) / [Zenn](https://zenn.dev/backpaper0)
- 独立系SIer勤務
- Spring Boot歴 10年弱
- オンサイト登壇: 2019年11月の[KanJava 10th Anniversary Party](https://kanjava.connpass.com/event/147145/)
- オンライン登壇: [2021年8月のJSUG勉強会](https://jsug.doorkeeper.jp/events/124798)、[Spring Fest 2023](https://springfest2023.springframework.jp/)

---

## 本日お話しすること

1. OpenAI APIを用いたテキスト生成の基本
1. Spring AIとOpenAI APIの組み合わせ方
1. ベクトルデータベースとRAG(Retrieval-Augmented Generation)
1. MCP(Model Context Protocol)で外部データソースを操作

---

## 発表資料・コード例のURL

- 発表資料
    - https://backpaper0.github.io/spring-ai-introduction/
- コード例
    - https://github.com/backpaper0/spring-ai-introduction

---

### バージョンなど

- Java 21
- Spring Boot 3.4.1
- Spring AI 1.0.0-M5
- OpenAI API
    - 使用しているモデルは次の通り
        - テキスト生成: `gpt-4o-mini`
        - 埋め込み: `text-embedding-3-small`

---

<!--
_class: lead
-->

## OpenAI APIを用いたテキスト生成の基本

---

### 生成AIによるテキスト生成について

* 生成AIは入力されたテキストの続きをそれっぽく生成する
    * <small>人「こんにちは。」</small>
      <small>AI「こんにちは！何かお手伝いできることはありますか？」←それっぽい</small>
* プロンプト = 入力されたテキスト
* トークン = テキストを意味的な単位(単語や記号、文字など)で分割したもの
    * gpt-4o-miniでトークン化した例
      <span class="t1">Spring</span> <span class="t2">Boot</span><span class="t3">で</span><span class="t4">遊</span><span class="t5">ぼ</span><span class="t6">う</span><span class="t7">！</span>
      [<span class="t1">30099</span>, <span class="t2">25087</span>, <span class="t3">4344</span>, <span class="t4">103923</span>, <span class="t5">128194</span>, <span class="t6">8574</span>, <span class="t7">3393</span>]

---

### テキスト生成するAPI

* [Create chat completion](https://platform.openai.com/docs/api-reference/chat/create)
* APIを使うためには
    - アカウントを作成
    - Credit balanceを追加(プリペイド)
    - APIキーを作成する

---

### 最初のテキスト生成

```bash
curl https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d '{
    "model": "gpt-4o-mini",
    "messages": [
      {
        "role": "user",
        "content": "こんにちは。"
      }
    ]
  }'
```

---

```json
{
  "id": "chatcmpl-ArKzNcOaW2qqrwpDzSbtgLJX9wg6T",
  "object": "chat.completion",
  "created": 1737275429,
  "model": "gpt-4o-mini-2024-07-18",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "こんにちは！何かお手伝いできることはありますか？",
        "refusal": null
      },
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 9,
    "completion_tokens": 16,
    "total_tokens": 25,
    "prompt_tokens_details": {
      "cached_tokens": 0,
      "audio_tokens": 0
    },
    "completion_tokens_details": {
      "reasoning_tokens": 0,
      "audio_tokens": 0,
      "accepted_prediction_tokens": 0,
      "rejected_prediction_tokens": 0
    }
  },
  "service_tier": "default",
  "system_fingerprint": "fp_72ed7ab54c"
}
```

---

### 複数のmessageで会話を表現できる

```bash
curl https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d '{
    "model": "gpt-4o-mini",
    "messages": [
      { "role": "user"     , "content": "1から10の中で数字を2つ挙げて。" },
      { "role": "assistant", "content": "2と5です。" },
      { "role": "user"     , "content": "それらの合計は？" }
    ]
  }' -s | jq -r ".choices[0].message.content"
```

<div data-marpit-fragment>

```
2と5の合計は7です。
```

</div>

---

### システムプロンプトで生成AIに指示を与える

```bash
curl https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d '{
    "model": "gpt-4o-mini",
    "messages": [
      { "role": "system", "content": "あなたは令和に生きる侍です。武を重んじ忠義を尽くす性格です。話し言葉も侍を意識してください。" },
      { "role": "user"  , "content": "こんにちは。" }
    ]
  }' -s | jq -r ".choices[0].message.content"
```

<div data-marpit-fragment>

```
こんにちは、御仁。今日は如何な様子でござるか？何かお手伝いできることがあれば、遠慮なきよう仰せください。
```

</div>

---

### 参考: コスト計算

- gpt-4o-miniは100万トークンあたり
    - 入力: $0.15
      出力: $0.60
      <small>[Pricing - OpenAI API](https://platform.openai.com/docs/pricing)</small>
- <small>最初のテキスト生成結果より</small>
  ```json
    "usage": {
      "prompt_tokens": 9,
      "completion_tokens": 16,
      "total_tokens": 25,
  ```
  <small>((9 × $0.15) + (16 × $0.65)) / 100万</small> = 0.001833円 <small>($1 = 156円)</small>

---

### 参考: 『吾輩は猫である』が出力された場合のコスト

[夏目漱石『吾輩は猫である』(青空文庫)](https://www.aozora.gr.jp/cards/000148/files/789_14547.html)

- 文字数: 347,704
- トークン数: 296,711
- <small>(296,711 × $0.65) / 100万 = $0.193</small> = 30円
- モデルによって価格は異なる
    - gpt-4oだと463円

---

<!--
_class: lead
-->

## Spring AIとOpenAI APIの組み合わせ方

---

### Spring AI

- AIアプリケーション開発の基盤となる機能を提供する
    - <small>LangChainやLlamaIndexといった著名なPythonプロジェクトからインスピレーションを得ているが、それらのクローンではない</small>
- 提供される機能
    - チャットや埋め込みのクライアント
    - 構造化された出力をJavaオブジェクトへマッピング
    - ベクトルデータベースのサポート

---

<!--
_class: lead
-->

![height:600px](class1.svg)

---

### チャットクライアントの構築

```java
@Bean
ChatClient chatClient(ChatClient.Builder builder) {
    String defaultSystem = """
            あなたは令和に生きる侍です。武を重んじ忠義を尽くす性格です。
            話し言葉も侍を意識してください。""";
    return builder
        .defaultSystem(defaultSystem)
        .defaultAdvisors(advisor1, advisor2, advisor3)
        .build();
}
```

- ビルダーをインジェクションして構築する
- 必要に応じてシステムプロンプトや`Advisor`のデフォルト値を設定する

---

### チャットクライアントでテキスト生成

```java
@PostMapping
String generate(@RequestParam String query) {
    return chatClient.prompt()
        .user(query)
        .call()
        .content();
}
```

---

### ストリーム(Server-Sent Events)

```java
@PostMapping(path = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Flux<String> stream(@RequestParam String query) {
    return chatClient.prompt()
        .user(query)
        .stream()
        .content();
}
```

- `call`メソッドの代わりに`stream`メソッドをコールする
- `Flux<T>`を取得できるので`text/event-stream`で返せばSSEできる

---

### システムプロンプト

```java
@PostMapping("with_system_prompt")
String generateWithSystemPrompt(@RequestParam String query) {
    String system = """
            あなたはJava言語のエキスパートです。Javaに関する質問へ自信満々に回答してください。
            回答するときは語尾を「じゃば」にしてください。""";
    return chatClient.prompt()
        .system(system)
        .user(query)
        .call()
        .content();
}
```

- `ChatClient`構築時にデフォルトのシステムプロンプトを設定できるが、アドホックな指定もできる

---

### マルチモーダル

```java
@PostMapping("multimodal")
String generateMultimodal(@RequestParam String query, @RequestParam URL image) {
    return chatClient.prompt()
        .user(prompt -> prompt
            .text(query)
            .media(MimeType.valueOf("image/jpeg"), image))
        .call()
        .content();
}
```

- マルチモーダル = テキスト、画像、音声など複数の入力モードを処理するやつ

---

### 構造化された出力

```java
record MainChatacters(String hero, String heroine, List<String> subCharacters) {
}
```

```java
@PostMapping
MainChatacters post(@RequestParam String query) {
    return chatClient.prompt()
        .user(query)
        .call()
        .entity(MainChatacters.class);
}
```

- 生成AIがJSONスキーマに従って出力してくれるやつ

---

### Advisor

- 処理をインターセプトして前後処理を差し込んだり入出力を加工できる仕組み
- FQCNは`org.springframework.ai.chat.client.advisor.api.Advisor`
- `org.springframework.aop.Advisor`とは継承関係にない、まったくの別物

---

<!--
_class: lead
-->

![height:600px](class2.svg)

---

### 会話の履歴

```java
public ChatController(ChatClient.Builder builder, ChatMemory chatMemory) {
    this.chatClient = builder
        .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
        .build();
}
```

- `MessageChatMemoryAdvisor`がクエリーのコンテキストとして会話の履歴を付与したり、クエリーと生成されたテキストを会話の履歴として保存してくれる
- `ChatMemory`は会話の履歴を登録・取得するインターフェース

---

### 会話の履歴

```java
@PostMapping("{conversationId}")
public String post(@RequestParam String query, @PathVariable String conversationId) {
    return chatClient.prompt()
        .user(query)
        .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
        .call()
        .content();
}
```

- `"chat_memory_conversation_id"`パラメーターで会話を指定する

---

<!--
_class: lead
-->

### チャットクライアントのデモ

---

### チャットクライアントのまとめ

- `ChatClient`でテキスト生成できる
    - インスタンスはビルダーで構築する
    - マルチモーダルにも対応している
- `Advisor`で処理を拡張できる
    - 実装クラスのひとつに会話の履歴を扱う`MessageChatMemoryAdvisor`がある

---

<!--
_class: lead
-->

## ベクトルデータベースとRAG<br><small>(Retrieval-Augmented Generation)</small>

---

### RAGの概要

- RAG（Retrieval-Augmented Generation）は生成AIが「事前に学んだ知識」だけでなく「外部の情報」を取り込んで回答を生成する仕組み
- 具体的には、まず質問に関連する情報をデータベースや検索エンジンから探し出し、その情報を元に回答を作る
- 例えるなら「本を読んで得た知識だけで話す」生成AIが「必要に応じて本棚から新しい本を取り出して確認しながら話す」ようになる感じ

---

### ベクトル検索の概要

- テキストや画像などのデータを数値ベクトルに変換し、ベクトル間の類似度を計算することで意味的に近いものを検索する技術
- 数値ベクトルへの変換には「埋め込みモデル」というものが使われる

---

### Spring AIでRAG

- RAGのための`Advisor`が2つある
    - `QuestionAnswerAdvisor`
        - ベクトルデータベースを検索した結果をプロンプトに埋め込んでテキスト生成するだけの単純なRAGができる
    - `RetrievalAugmentationAdvisor`
        - クエリーの変換や拡張、ルーティングといったRAGの性能を向上させるための戦略を適用できる
- 個人的には、まだ実験的なものではあるものの`RetrievalAugmentationAdvisor`の方がオススメ

---

<!--
_class: lead
-->

![height:600px](class3.svg)

---

### RetrievalAugmentationAdvisor

```java
var ragAdvisor = RetrievalAugmentationAdvisor.builder()
        .queryTransformers(...)
        .queryExpander(...)
        .queryRouter(...)
        .documentRetriever(...)
        .documentJoiner(...)
        .queryAugmenter(...)
        .build();

this.chatClient = chatClientBuilder.defaultAdvisors(ragAdvisor).build();
```

---

### RetrievalAugmentationAdvisor

- `QueryTransformer`
    - <small>例）日本語で書かれたクエリーを英語に翻訳する</small>
- `QueryExpander`
    - <small>例）クエリーのバリエーションを増やす(マルチクエリー)</small>
        - <small>うらがみさんてどんな人？</small>
            - <small>うらがみさんの趣味は？</small>
            - <small>うらがみさんの職業は？</small>
            - <small>うらがみさんの出身は？</small>

---

### RetrievalAugmentationAdvisor

- `QueryRouter`
    - <small>例）クエリーの内容を見てベクトルデータベースを切り替える</small>
- `DocumentRetriever`
    - <small>例）ベクトル検索を行い、ドキュメントを返す</small>
- `DocumentJoiner`
    - <small>例）複数のドキュメントを改行区切りでひとつにまとめる</small>
- `QueryAugmenter`
    - <small>例）クエリーにコンテキストを付与する</small>

---

### RetrievalAugmentationAdvisor

- 単純なRAGであれば`DocumentRetriever`と`QueryAugmenter`を設定すれば良い

```java
DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
    .vectorStore(vectorStore).build();

QueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder().build();

Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
    .documentRetriever(documentRetriever)
    .queryAugmenter(queryAugmenter)
    .build();
this.chatClient = builder.defaultAdvisors(ragAdvisor).build();
```

---

<!--
_class: lead
-->

### RAGのデモ

---

### RAGのまとめ

- RAGは生成AIが持ち合わせていない知識を補う手法
- 知識は埋め込みモデルによって計算された数値ベクトルと類似度検索で取得される
- Spring AIでは`QuestionAnswerAdvisor`または`RetrievalAugmentationAdvisor`でRAGできる
    - うらがみ的には大は小を兼ねるの精神から`RetrievalAugmentationAdvisor`がオススメ

---

<!--
_class: lead
-->

## MCP<small>(Model Context Protocol)</small>で<br>外部データソースを操作

---

### MCP<small>(Model Context Protocol)</small>の概要

- アプリケーションが生成AIにコンテキストを提供する方法を標準化するオープンプロトコル
- 生成AIを様々なデータソースやツールに接続するための標準化された方法

---

<!--
_class: lead
-->

![height:500px](mcp_architecture.png)

<small>出典: https://modelcontextprotocol.io/introduction#general-architecture</small>

---

### Spring AI MCP

- Spring AIでMCPを扱うための実験的なプロジェクト
- https://github.com/spring-projects-experimental/spring-ai-mcp

---

<!--
_class: lead
-->

![height:500px](spring_ai_mcp_architecture.png)

<small>出典: https://docs.spring.io/spring-ai-mcp/reference/overview.html</small>

---

### Spring AI MCP Client

- `FunctionCallback`を介して透過的に使える
- <small>※MCPまわりは絶賛コードリーディング中なので説明が雑</small>

---

<!--
_class: lead
-->

![height:600px](class4.svg)

---

### MCPでPostgreSQLへ接続する例

```java
ServerParameters params = ServerParameters.builder("npx")
    .args(
        "-y",
        "@modelcontextprotocol/server-postgres",
        "postgres://myuser:secret@localhost:5432/mydatabase")
    .build();

McpTransport transport = new StdioClientTransport(params);

McpSyncClient mcpClient = McpClient.using(transport).sync();
mcpClient.initialize();
```

---

### MCPでPostgreSQLへ接続する例

```java
McpFunctionCallback[] callbacks = mcpClient.listTools()
    .tools()
    .stream()
    .map(tool -> new McpFunctionCallback(mcpClient, tool))
    .toArray(McpFunctionCallback[]::new);

this.chatClient = builder.defaultFunctions(callbacks)
    .build();
```

---

<!--
_class: lead
-->

### MCPでPostgreSQLへ接続するデモ

---

### MCPのまとめ

- MCPを使えばアプリケーションへコンテキストを提供できる
- Spring AIもMCPに対応していて、例えばPostgreSQLからデータを取得してコンテキストとしてプロンプトに付与できる
- 今回紹介したのはMCP Clientだけだが、MCP Serverの実装もできる

---

## 総括

* 生成AIをシステムに組み込む手段はPythonだけではない
* JavaであればSpring AIが有力な候補となると感じた
* それはそれとして、Spring AIのようにSpringに新規モジュールが出てくるのは嬉しい
* Springのおかげで新しい技術に触れることを楽しめている気がする
* オマエたち、オレと一緒にSpring AIで遊ぼうぜ！

<div data-marpit-fragment>
🔚
</div>

---

<!--
_class: lead
-->

## Appendix

---

<!--
_class: lead
-->

![height:600px](class0.svg)

---

- 公式リファレンス
    - [Spring AI リファレンス](https://docs.spring.io/spring-ai/reference/)
        - <small>今回紹介しなかった"ETL Pipeline"（RAGのためのデータ準備）や"Evaluation Testing"（生成された応答の評価）も必見</small>
    - [Java & Spring MCP リファレンス](https://docs.spring.io/spring-ai-mcp/reference/overview.html)
        - <small>次はMCP Serverを実装して紹介したい</small>
- GitHub
    - [spring-projects/spring-ai](https://github.com/spring-projects/spring-ai)
    - [spring-projects/spring-ai-examples](https://github.com/spring-projects/spring-ai-examples)
    - [spring-projects-experimental/spring-ai-mcp](https://github.com/spring-projects-experimental/spring-ai-mcp)
