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
    @Test
    void testEmbeddingText()
    {
        var embeded=aiService.getEmbedding("spring boot is a v=big eneterpis elevel framegereokbdjhbcjhebjf kjekjfhekjf");
        System.out.println(embeded.length);
        for(float e:embeded)
        {
            System.out.println(e + "");
        }
    }
    @Test
    void testTnjestDataToVectorStore()
    {
        aiService.injestDataToVectorStore();
    }

//    @Test
//    void testAskAi()
//    {
//        var response=aiService.askAi("Explain Consistent Hashing and why is used.");
//        log.info(response);
//    }
}
