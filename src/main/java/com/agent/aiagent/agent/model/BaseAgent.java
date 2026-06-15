package com.agent.aiagent.agent.model;

import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * 抽象基础代理类，用于管理代理状态和执行流程
 * 子类只需要实现 step 方法
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 代理状态
    private AgentState state = AgentState.IDLE;

    // 执行步数控制
    private int maxSteps = 10;
    private int currentStep = 0;

    // LLM 大模型
    private ChatClient chatClient;

    // Memory 记忆
    private List<Message> messageList = new ArrayList<>();

    // SSE 流式输出
    protected transient volatile SseEmitter currentEmitter;

    protected void emitStream(String text) {
        SseEmitter emitter = this.currentEmitter;
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(text);
        } catch (Exception e) {
            log.warn("发送流式消息失败: {}", e.getMessage());
        }
    }

    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }

        this.state = AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));

        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                currentStep = i + 1;
                step();
            }

            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
            }

            return "执行完成";
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误: " + e.getMessage();
        } finally {
            this.cleanup();
        }
    }

    /*
     * 运行代理（流式输出）
     */
    public SseEmitter runStream(String userPrompt) {
        SseEmitter emitter = new SseEmitter(300000L);
        this.currentEmitter = emitter;

        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    emitter.send("错误：无法从当前状态运行代理 " + this.state);
                    emitter.complete();
                    return;
                }
                if (StringUtil.isBlank(userPrompt)) {
                    emitter.send("错误：不能使用空提示词运行代理");
                    emitter.complete();
                    return;
                }

                state = AgentState.RUNNING;
                messageList.add(new UserMessage(userPrompt));

                emitStream("正在分析你的问题...");

                try {
                    for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                        currentStep = i + 1;
                        emitStream("正在执行第 " + currentStep + " 步...");
                        step();
                    }

                    if (currentStep >= maxSteps && state != AgentState.FINISHED) {
                        state = AgentState.FINISHED;
                        emitStream("任务已达到最大步数，自动结束。");
                    } else if (state == AgentState.FINISHED) {
                        emitStream("任务已完成。");
                    }

                    emitter.complete();
                } catch (Exception e) {
                    state = AgentState.ERROR;
                    log.error("执行智能体失败", e);
                    try {
                        emitter.send("执行错误: " + e.getMessage());
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                } finally {
                    this.cleanup();
                    this.currentEmitter = null;
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            this.currentEmitter = null;
            log.warn("SSE connection timed out");
        });

        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            this.currentEmitter = null;
            log.info("SSE connection completed");
        });

        return emitter;
    }

    public abstract String step();

    protected void cleanup() {
    }
}