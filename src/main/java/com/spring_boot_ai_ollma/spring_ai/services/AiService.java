package com.spring_boot_ai_ollma.spring_ai.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    private final ChatClient chatClient;

    public AiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    public String sayHello(String topic) {
        return chatClient.prompt().user("tell me about this this framework and why it is used "+topic).call().content();
    }
}
