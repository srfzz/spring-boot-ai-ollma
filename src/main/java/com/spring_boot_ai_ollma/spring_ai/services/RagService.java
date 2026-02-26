package com.spring_boot_ai_ollma.spring_ai.services;


import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class RagService {
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    @Value("classpath:javabook.pdf")
    Resource resource;

    public void injestPdfDataToVectorStore() {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);
        List<Document> pages = reader.get();

        TokenTextSplitter splitter = new TokenTextSplitter(800, 100, 5, 10000, true);

        Set<String> existingHashes = new HashSet<>(); // store hashes to detect duplicates

        List<Document> enrichedPages = pages.parallelStream()
                .flatMap(page -> splitter.apply(List.of(page)).parallelStream())
                .map(chunk -> {
                    String cleanedContent = sanitizeForPostgres(chunk.getText());
                    if (cleanedContent == null || cleanedContent.isBlank()) return null;

                    Map<String, Object> newMetadata = new HashMap<>(chunk.getMetadata());
                    newMetadata.put("ingested_at", java.time.LocalDateTime.now().toString());
                    newMetadata.put("source_file", resource.getFilename());
                    newMetadata.put("content_type", "technical_documentation");

                    return new Document(cleanedContent, newMetadata);
                })
                .filter(Objects::nonNull)
                .filter(doc -> {
                    String hash = doc.getText() + "|" + doc.getMetadata().get("source_file");
                    synchronized (existingHashes) {
                        if (existingHashes.contains(hash)) return false;
                        existingHashes.add(hash);
                        return true;
                    }
                })
                .collect(Collectors.toList());

        vectorStore.add(enrichedPages);
        System.out.println("Inserted unique documents: " + enrichedPages.size());
    }

    private String sanitizeForPostgres(String input) {
        if (input == null) return "";
        return input
                .replace("\u0000", "")
                .replaceAll("[\\x00]", "")
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .trim();
    }

    public String askAi(String prompt) {
        String systemTemplate = """
    You are a helpful AI assistant for a developer.
    
    Use the following context from our private database to answer the question:
    ---------------------
    {context}
    ---------------------
    
    RULES FOR ANSWERING:
    1. Use ONLY the information provided in {context}.
    2. Provide clear, complete explanations, not just one-word answers.
    3. Explain concepts, steps, or code logic as if teaching a developer.
    4. Do NOT include phrases like "according to the context" or "the text says".
    5. Include examples or code snippets if they are present in the context.
    6. If the answer is not in the context, respond with:
       "I'm sorry, I don't have that information in my knowledge base."
    7. Keep answers concise but informative; avoid unnecessary filler.
""";
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(prompt).build());
        String context = documents.stream()
                .map(doc -> "Content: " + doc.getText() +
                        "\n(Source: " + doc.getMetadata().get("source_file") +
                        ", Page: " + doc.getMetadata().get("page_number") + ")")
                .collect(Collectors.joining("\n\n"));
        PromptTemplate promptTemplate = new PromptTemplate(systemTemplate);
        String SystemPrompt=promptTemplate.render(Map.of("context", context));
        return chatClient.prompt().system(SystemPrompt).user(prompt).call().content();
    }


}
