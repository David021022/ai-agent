package com.agent.aiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;

public class LangChainAiInvoke {
    public static void main(String[] args) {
        ChatModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApikey.API_KEY)
                .modelName("qwen-max")
                .build();
        String answer = qwenChatModel.chat("你好");
        System.out.println(answer);

    }
}
