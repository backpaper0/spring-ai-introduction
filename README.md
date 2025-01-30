# Spring AI introduction

## 概要

Spring AIを紹介する発表資料とコード例です。

## コード例

### 動作環境

- Java 21
- Node.js v22
    - MCPの例で使用（発表資料のビルドでも使用）
- Maven 3
- Docker / Docker Compose
    - 筆者はOrbStackを使用
- OpenAI API
    - 使用しているモデルは次の通り
        - テキスト生成: `gpt-4o-mini`
        - 埋め込み: `text-embedding-3-small`

### 事前準備

OpenAI APIのAPIキーを払い出して`spring.ai.openai.api-key`プロパティへ設定します。

### アプリケーションの起動

```bash
mvn spring-boot:run
```

### 動作確認

- シンプルなテキスト生成

  ```bash
  curl http://localhost:8080/hello -d query="100文字程度でSpring Bootを説明して。"
  ```

- Server-Sent Events版

  ```bash
  curl http://localhost:8080/hello/stream -d query="100文字程度でSpring Bootを説明して。"
  ```

- システムプロンプトを指定したテキスト生成

  ```bash
  curl http://localhost:8080/hello/with_system_prompt -d query="100文字程度でSpring Bootを説明して。"
  ```

- テキストと画像のマルチモーダル

  ```bash
  curl http://localhost:8080/multimodal -d query="説明して。" -d image="https://avatars.githubusercontent.com/u/209262?v=4"
  ```

- 構造化された出力

  ```bash
  curl http://localhost:8080/structure -d query="ゼルダの伝説ブレスオブザワイルドの主人公とヒロイン、それから代表的なサブキャラクターを3人教えてください。" -s | jq
  ```

- 会話

  ```bash
  curl http://localhost:8080/chat/1 -d query="100文字程度でSpring Bootを説明して。"
  ```

  ```bash
  curl http://localhost:8080/chat/1 -d query="Hello Worldして。"
  ```

  ```bash
  curl http://localhost:8080/chat/1 -d query="データベースも使いたい。"
  ```

- RAG

  ```bash
  curl http://localhost:8080/rag -d query="うらがみさんはどこ住み？"
  ```

  ```bash
  curl http://localhost:8080/rag -d query="うらがみさんの職業は？"
  ```

- MCPでPostgreSQLを操作

  ```bash
  curl http://localhost:8080/mcppg/2 -d query="PostgreSQLへ接続してどんなテーブルがあるか教えて。"
  ```

  ```bash
  curl http://localhost:8080/mcppg/2 -d query="shopping_historyってどんなテーブル？"
  ```

  ```bash
  curl http://localhost:8080/mcppg/2 -d query="一番高い購入品は？"
  ```

  ```bash
  curl http://localhost:8080/mcppg/2 -d query="他のリコリス・リコイルに関する購入品を教えて。"
  ```

## 発表資料のビルド

まずMermaidのCLIでクラス図をビルドします。

```bash
ls src/docs/*.mmd | while read a; do npx -y @mermaid-js/mermaid-cli -i ${a} -o docs/$(basename ${a%.*}).svg; done
```

それからMarpのCLIで発表資料をビルドします。

```bash
npx @marp-team/marp-cli@latest --html --output docs/index.html --theme src/docs/theme.css src/docs/slide.md
```

このGitHubリポジトリでは`push`すると`docs/index.html`へ書き出されてGitHub Pagesでホスティングされます。

- https://backpaper0.github.io/spring-ai-introduction/ 

なお発表資料のビルドに関して、執筆中はウォッチモードが便利です。

```bash
npx @marp-team/marp-cli@latest --html --output docs/index.html --theme src/docs/theme.css src/docs/slide.md --watch
```

## ライセンス

発表資料(`docs/`配下にあるファイル)は[CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)、ソースコード(発表資料以外のファイル)は[MIT](https://opensource.org/licenses/mit-license.php)を適用します。
