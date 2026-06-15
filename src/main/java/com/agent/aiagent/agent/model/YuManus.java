package com.agent.aiagent.agent.model;

import com.agent.aiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class YuManus extends ToolCallAgent {

    public YuManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("yuManus");

        String SYSTEM_PROMPT = """
                你是 YuManus，一位全能的 AI 助手，旨在解决用户提出的任何任务。
                你拥有多种工具可供调用，能够高效地完成复杂的请求。""";
        this.setSystemPrompt(SYSTEM_PROMPT);

        String NEXT_STEP_PROMPT = """
                你需要基于用户需求，主动选择最合适的单一工具或工具组合。面对复杂任务时，你可以将其拆解，并分步调用不同的工具来解决。每次使用工具后，请清晰地说明执行结果，并建议后续的步骤。
                如果你在任何节点决定终止交互，请使用 terminate工具/函数进行调用。""";
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);

        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}