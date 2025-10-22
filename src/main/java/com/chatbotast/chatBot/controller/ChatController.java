// File: src/main/java/com/chatbotast/chatBot/controller/ChatController.java
package com.chatbotast.chatBot.controller;

import com.chatbotast.chatBot.model.ChatRequest;
import com.chatbotast.chatBot.model.ChatResponse;
import com.chatbotast.chatBot.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private GeminiService geminiService;  

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            logger.info("Received chat request");

            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(ChatResponse.error("Prompt is required"));
            }

            String response = geminiService.generateResponse(request.getPrompt());

            logger.info("Successfully processed chat request");
            return ResponseEntity.ok(new ChatResponse(response));

        } catch (Exception e) {
            logger.error("Error processing chat request", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ChatResponse.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"OK\",\"service\":\"Chatbot Backend\"}");
    }
}