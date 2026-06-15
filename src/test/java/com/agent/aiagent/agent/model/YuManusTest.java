package com.agent.aiagent.agent.model;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class YuManusTest {

    @Resource
    private YuManus yuManus;

    @Test
    void run() {
        String userPrompt = """
              帮我搜一下苏州吴中区附近的美食""";
        String answer = yuManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
