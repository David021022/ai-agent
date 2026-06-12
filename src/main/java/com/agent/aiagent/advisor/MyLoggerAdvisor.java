package com.agent.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("AI Request: {}", chatClientRequest.prompt().getContents());
        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);
        log.info("AI Response: {}", response.chatResponse().getResult().getOutput().getText());
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        log.info("AI Request: {}", chatClientRequest.prompt().getContents());
        return streamAdvisorChain.nextStream(chatClientRequest)
                .doOnNext(response -> log.info("AI Response: {}", response.chatResponse().getResult().getOutput().getText()));
    }
}
