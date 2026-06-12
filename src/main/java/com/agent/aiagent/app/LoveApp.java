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
            扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。
            围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；
            恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。
            引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。
            当用户要求搜索图片、找图片、推荐图片链接时，必须调用 MCP 图片搜索工具，不要只用文字回答。
            返回结果时直接给出图片链接或 Markdown 图片列表，不要只做概括。
            """;

    private static final String LOVE_REPORT_PROMPT = """
            每次对话后都要生成恋爱报告。
            请严格输出可映射为 LoveReport 的结构化内容。
            title 格式为：{用户名}的恋爱报告。
            suggestions 只输出建议列表，数量控制在 3 到 5 条，每条简短、具体、可执行。
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
