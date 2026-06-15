package com.agent.aiagent.agent.model;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 处理工具调用的基础代理类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存工具调用信息的响应结果
    private ChatResponse toolCallChatResponse;

    // 工具调用管理器
    private final ToolCallingManager toolCallingManager;

    // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
    }

    @Override
    public boolean think() {
        emitStream("正在分析任务并判断是否需要调用工具...");

        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }

        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);

        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();

            this.toolCallChatResponse = chatResponse;

            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();

            if (toolCallList.isEmpty()) {
                getMessageList().add(assistantMessage);
                emitStream("模型直接给出了答案。");
                emitStream(assistantMessage.getText());
                return false;
            }

            String toolNames = toolCallList.stream()
                    .map(AssistantMessage.ToolCall::name)
                    .collect(Collectors.joining("、"));

            emitStream("准备调用工具：" + toolNames);
            return true;
        } catch (Exception e) {
            log.error(getName() + " 的思考过程遇到了问题: " + e.getMessage());
            emitStream("思考阶段出错：" + e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        if (toolCallChatResponse == null || !toolCallChatResponse.hasToolCalls()) {
            emitStream("没有工具调用，直接结束。");
            return "没有工具调用";
        }

        String toolNames = toolCallChatResponse.getResult().getOutput().getToolCalls().stream()
                .map(AssistantMessage.ToolCall::name)
                .collect(Collectors.joining("、"));
        emitStream("正在执行工具：" + toolNames);

        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult =
                toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);

        setMessageList(toolExecutionResult.conversationHistory());

        ToolResponseMessage toolResponseMessage =
                (ToolResponseMessage) toolExecutionResult.conversationHistory()
                        .get(toolExecutionResult.conversationHistory().size() - 1);

        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));

        if (terminateToolCalled) {
            setState(AgentState.FINISHED);
            emitStream("收到终止指令，任务即将结束。");
        }

        emitStream("工具执行完成，正在整理最终答案...");

        ChatResponse finalResponse = getChatClient().prompt(
                        new Prompt(getMessageList(), chatOptions))
                .system(getSystemPrompt())
                .call()
                .chatResponse();

        AssistantMessage finalMessage = finalResponse.getResult().getOutput();
        getMessageList().add(finalMessage);

        emitStream(finalMessage.getText());
        return finalMessage.getText();
    }
}