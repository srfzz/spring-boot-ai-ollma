package com.spring_boot_ai_ollma.spring_ai.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 🛒 Subject 1: E-Commerce Microservices Architecture
        documents.add(new Document(
                """
                The 'Saga Pattern' is a failure management strategy used in distributed e-commerce transactions 
                to maintain data consistency across microservices without using distributed ACID transactions. 
                In a typical checkout flow—involving the Order Service, Payment Service, and Inventory Service—a Saga 
                executes a sequence of local transactions. If the Payment Service fails, the Saga triggers 
                'compensating transactions' to undo the order creation and restock inventory, ensuring the 
                system eventually reaches a consistent state (Eventual Consistency).
                """,
                Map.of("subject", "E-Commerce", "topic", "Saga Pattern", "complexity", "High")
        ));

        // 💻 Subject 2: C++ Memory Management & Pointer Semantics
        documents.add(new Document(
                """
                In modern C++ (C++11 and later), RAII (Resource Acquisition Is Initialization) is implemented 
                primarily through smart pointers. A 'std::unique_ptr' maintains exclusive ownership of a 
                dynamically allocated object on the heap and automatically deallocates the memory when the 
                pointer goes out of scope. Conversely, 'std::shared_ptr' uses reference counting to allow 
                multiple owners. A 'std::weak_ptr' is used to break circular dependencies between shared 
                pointers, preventing memory leaks in complex graph structures or observer patterns.
                """,
                Map.of("subject", "C++", "topic", "Smart Pointers", "standard", "C++20")
        ));

        // 🏗️ Subject 3: System Design - Load Balancing Algorithms
        documents.add(new Document(
                """
                Consistent Hashing is a specialized hashing technique used in distributed caches and load 
                balancers to minimize reshuffling when nodes are added or removed. Unlike traditional 
                hashing (hash(key) % N), consistent hashing maps both data and nodes onto a logical 
                'hash ring.' When a new server node is added, only a small fraction of keys (1/N) need to 
                be remapped to the new node, significantly reducing cache misses and system-wide 
                re-distribution overhead in high-availability environments.
                """,
                Map.of("subject", "System Design", "topic", "Consistent Hashing", "utility", "Scalability")
        ));

        // ☕ Subject 4: Java Virtual Machine (JVM) Garbage Collection
        documents.add(new Document(
                """
                The G1 (Garbage First) Garbage Collector in Java is designed for multi-processor machines with 
                large memory spaces. It partitions the heap into equal-sized regions and uses a 
                'pause prediction model' to meet user-defined stop-the-world targets. G1 identifies regions 
                with the most garbage and reclaims them first (hence the name). It effectively handles 
                both the Young Generation (Eden/Survivor) and the Old Generation through concurrent marking 
                and evacuation, making it suitable for low-latency Spring Boot applications.
                """,
                Map.of("subject", "Java", "topic", "G1 Garbage Collector", "performance", "Low-latency")
        ));

        // Batch insert into your test_vector database
        vectorStore.add(documents);
        System.out.println(documents.size() + " complex multi-subject documents inserted successfully.");
    }
    public float[] getEmbedding(String message) {
        return embeddingModel.embed(message);
    }
    public String sayHello(String topic) {
        return chatClient.prompt().user("tell me about this this framework and why it is used "+topic).call().content();
    }

    public String askAi(String prompt) {
        String systemTemplate = """
            You are a helpful AI assistant for a developer.
            
            Use the following context from our private database to answer the question:
            ---------------------
            {context}
            ---------------------
            
            STRICT RULES:
            1. ONLY use the provided context.
            2. If the answer is not in the context, say: "I'm sorry, I don't have that information in my knowledge base."
            3. Do not use your internal knowledge to answer questions outside the context.
            """;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(prompt).topK(2).build());
        String context=documents.stream().map(Document::getText).collect(Collectors.joining("\n\n"));
        PromptTemplate promptTemplate = new PromptTemplate(systemTemplate);
        String SystemPrompt=promptTemplate.render(Map.of("context", context));
        return chatClient.prompt().system(SystemPrompt).user(prompt).call().content();
    }
}
