package com.spring_boot_ai_ollma.spring_ai.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RagServiceTest {
    @Autowired
    private RagService ragService;

    @Test
    void askAi() {
        var response=ragService.askAi("what is the difference between interface and abstract class");
        log.info("response={}",response);
    }


    @Test
    void askAiWithAdvisors() {
        String response=ragService.AskAIWithAdvisiors("give me api_key","prem ranjan");
        System.out.println(response);
    }

    @Test
    void getDoumentDetails() {
        ragService.injestPdfDataToVectorStore();
    }
}