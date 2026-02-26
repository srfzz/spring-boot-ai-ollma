package com.spring_boot_ai_ollma.spring_ai.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiService {
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore  vectorStore;

    public AiService(ChatClient chatClient, EmbeddingModel embeddingModel, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }
    public void injestDataToVectorStore() {

        List<Document> documents = new ArrayList<>();


        documents.add(new Document(
                "Section 80C allows tax deduction up to 1.5 lakh.",
                Map.of(
                        "manual", "Income Tax Act 1961",
                        "title", "Section 80C",
                        "page", 243
                )
        ));


        documents.add(new Document(
                "Section 10 deals with exemptions under income tax.",
                Map.of(
                        "manual", "Income Tax Act 1961",
                        "title", "Section 10",
                        "page", 101
                )
        ));


        documents.add(new Document(
                "GST Act defines supply under section 7.",
                Map.of(
                        "manual", "GST Act 2017",
                        "title", "Section 7 - Supply",
                        "page", 55
                )
        ));

        // Document 4
        documents.add(new Document(
                "Companies Act defines director responsibilities.",
                Map.of(
                        "manual", "Companies Act 2013",
                        "title", "Director Duties",
                        "page", 312
                )
        ));


        documents.add(new Document(
                "Labour Act defines employee rights and benefits.",
                Map.of(
                        "manual", "Labour Law Manual",
                        "title", "Employee Rights",
                        "page", 88
                )
        ));

        // 🔥 Batch insert
        vectorStore.add(documents);

        System.out.println("5 documents inserted successfully.");
    }
    public float[] getEmbedding(String message) {
        return embeddingModel.embed(message);
    }
    public String sayHello(String topic) {
        return chatClient.prompt().user("tell me about this this framework and why it is used "+topic).call().content();
    }
}
