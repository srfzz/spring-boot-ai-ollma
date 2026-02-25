package com.spring_boot_ai_ollma.spring_ai.service;


import com.spring_boot_ai_ollma.spring_ai.services.AiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AiServiceTest {
    @Autowired
     private AiService aiService;

    @Test
    public void testGetJoke()
    {
        String topic="spring boot";
   var joke = aiService.sayHello(topic);
   log.info(joke);
    }
}
