package com.spring_boot_ai_ollma.spring_ai.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    public AiService(ChatClient chatClient, EmbeddingModel embeddingModel) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
    }
    public float[] getEmbedding(String message) {
        return embeddingModel.embed(message);
    }
    public String sayHello(String topic) {
        return chatClient.prompt().user("tell me about this this framework and why it is used "+topic).call().content();
    }
}
