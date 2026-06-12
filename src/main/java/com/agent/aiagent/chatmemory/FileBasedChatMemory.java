package com.agent.aiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import dev.langchain4j.model.input.PromptTemplate;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBasedChatMemory implements ChatMemory {

    private static final Kryo KRYO = new Kryo();

    static {
        KRYO.setRegistrationRequired(false);
        KRYO.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    private final Path baseDir;

    public FileBasedChatMemory(String dir) {
        this.baseDir = Path.of(dir);
        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create chat memory directory: " + dir, e);
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        List<Message> conversationMessages = readConversation(conversationId);
        conversationMessages.addAll(messages);
        writeConversation(conversationId, conversationMessages);
    }

    @Override
    public List<Message> get(String conversationId) {
        return readConversation(conversationId);
    }

    @Override
    public void clear(String conversationId) {
        try {
            Files.deleteIfExists(getConversationFile(conversationId));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to clear conversation: " + conversationId, e);
        }
    }

    private List<Message> readConversation(String conversationId) {
        Path file = getConversationFile(conversationId);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }

        try (Input input = new Input(Files.newInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<Message> messages = KRYO.readObject(input, ArrayList.class);
            return new ArrayList<>(messages);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read conversation: " + conversationId, e);
        }
    }

    private void writeConversation(String conversationId, List<Message> messages) {
        Path file = getConversationFile(conversationId);
        try (Output output = new Output(Files.newOutputStream(file))) {
            KRYO.writeObject(output, new ArrayList<>(messages));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write conversation: " + conversationId, e);
        }
    }

    private Path getConversationFile(String conversationId) {
        return baseDir.resolve(conversationId + ".kryo");
    }
}
