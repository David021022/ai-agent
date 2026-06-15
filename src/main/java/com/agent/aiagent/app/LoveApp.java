package com.agent.aiagent.app;


import com.agent.aiagent.advisor.MyLoggerAdvisor;
import com.agent.aiagent.chatmemory.FileBasedChatMemory;
import com.agent.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
//import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import reactor.core.publisher.Flux;

import java.util.List;

//import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
//import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private static final String SYSTEM_PROMPT = """
            你是“AI旅游规划大师”，擅长为用户提供专业、实用、可执行的旅行规划建议。
                        
            你的职责包括：
            - 根据用户的出发地、目的地、天数、预算、人数、出行时间、偏好，生成旅行方案
            - 提供目的地推荐、路线设计、行程安排、交通建议、住宿建议、餐饮建议
            - 根据季节、天气、节假日、签证、时差、交通便利性、预算等因素做综合判断
            - 针对自由行、跟团游、亲子游、情侣游、老人出游、独自旅行、商务出行给出不同建议
            - 提醒用户注意旅行风险、踩坑点、天气变化、订票时机、行李准备、证件要求、当地习惯
            - 当需要联网搜索、文件处理、PDF 生成或调用工具时，优先使用可用工具，再基于工具结果继续回答
                        
            回答要求：
            - 先确认关键信息，不足时主动追问最重要的 1 到 3 个问题
            - 优先给出可直接执行的方案，而不是空泛建议
            - 尽量输出结构清晰、可落地的内容
            - 对于不确定的信息，要明确说明不确定，并建议用户核实
            - 不要编造交通、签证、景点开放时间、票价等可能变化的信息
            - 如果用户没有提供预算、出行时间、人数，先给出通用方案，再说明如何按条件调整
            - 语气专业、亲切、有条理，像一位经验丰富的旅行策划师。
            """;

    private static final String LOVE_REPORT_PROMPT = """
            每次对话后都要生成一份旅行规划报告。
            请严格输出可映射为 `TravelReport` 的结构化内容。
                        
            title 格式为：{用户名称}的旅行规划报告
                        
            suggestions 只输出建议列表，数量控制在 3 到 5 条，每条简短、具体、可执行。
                        
            建议内容应尽量覆盖：
            - 目的地选择或优化建议
            - 行程安排建议
            - 交通建议
            - 住宿建议
            - 预算控制建议
            - 行前准备或避坑建议
                        
            不要输出与 title 和 suggestions 无关的内容。
            
            """;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    /*多轮会话记忆*/

    public LoveApp(ChatModel dashscopeChatModel) {
        //支持文件会话记忆持久化的序列化
      /*  String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        this.chatMemory = new FileBasedChatMemory(fileDir);*/

         this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();

        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
    }

    /*
    * AI基础对话（支持多轮对话记忆）*/
    public String doChat(String message, String chatId) {
        String content = chatClient
                .prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(chatId)
                        .build())
                .call()
                .content();
        log.info("content: {}", content);
        return content;
    }

    /*
     * AI基础对话（支持多轮对话记忆，SSE流式传输）*/
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(chatId)
                        .build())
                .stream()
                .content();
    }

    //调用工具并流式输出
    public Flux<String> doChatWithToolsByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(chatId)
                        .build())
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .stream()
                .content();
    }



    /*结构化输出报告*/
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + LOVE_REPORT_PROMPT)
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(chatId)
                        .build())
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    public record LoveReport(String title, List<String> suggestions) {
    }

    /*AI恋爱知识库问答功能*/
    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

/*    @Resource
    private VectorStore pgVectorStore;*/


    /*和RAG知识库进行对话*/
    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
       /*         .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
*/
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(chatId)
                        .build())
                //开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())

                //应用RAG知识库问答
               //.advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .vectorStore(loveAppVectorStore)
                                .build())
                        .build())
                //应用RAG检索增强服务(基于云知识库）
           //     .advisors(loveAppRagCloudAdvisor)
                //应用RAG检索增强服务（基于pgwriter向量存储）

               // .advisors(new QuestionAnswerAdvisor(pgVectorStore))

                //应用自定义的RAG检索增强服务（文档查询器＋上下文增强）
     /*           .advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                                loveAppVectorStore, "已婚"
                        )
                )*/
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
    /*AI恋爱知识库调用工具能力*/
    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(chatId)
                        .build())

                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /*AI恋爱知识库调用MCP能力*/
    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(chatId)
                        .build())

                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


}
