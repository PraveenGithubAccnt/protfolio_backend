// File: src/main/java/com/chatbotast/chatBot/service/GeminiService.java
package com.chatbotast.chatBot.service;

import com.chatbotast.chatBot.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    @Autowired(required = false) // Make it optional for now
    private PromptBuilderService promptBuilderService;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateResponse(String userQuery) {
        try {
            logger.info("Generating response for user query");


            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
                throw new RuntimeException(
                        "Gemini API key is not configured. Please set GEMINI_API_KEY environment variable.");
            }


            String prompt;
            if (promptBuilderService != null) {
                prompt = promptBuilderService.buildPrompt(userQuery);
                logger.debug("Built prompt with portfolio context");
            } else {

                prompt = userQuery;
                logger.warn("PromptBuilderService not available, using direct query");
            }

            // Build request URL with API key
            String requestUrl = apiUrl + "?key=" + apiKey;

            // Create Gemini request
            GeminiRequest geminiRequest = buildGeminiRequest(prompt);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(geminiRequest, headers);

            // Make API call
            logger.debug("Calling Gemini API...");
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class);

            // Extract response text
            GeminiResponse geminiResponse = response.getBody();

            if (geminiResponse == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            if (geminiResponse.getError() != null) {
                throw new RuntimeException("Gemini API error: " + geminiResponse.getError().getMessage());
            }

            if (geminiResponse.getCandidates() == null || geminiResponse.getCandidates().isEmpty()) {
                throw new RuntimeException("No candidates in Gemini response");
            }

            String generatedText = geminiResponse.getCandidates().get(0)
                    .getContent()
                    .getParts().get(0)
                    .getText();

            // Add to conversation history if service is available
            if (promptBuilderService != null) {
                promptBuilderService.addToHistory(userQuery, generatedText);
            }

            logger.info("Successfully generated response");
            return generatedText.trim();

        } catch (HttpClientErrorException e) {
            logger.error("Client error calling Gemini API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Server error calling Gemini API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Gemini API server error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error generating response", e);
            throw new RuntimeException("Error generating response: " + e.getMessage());
        }
    }

    private GeminiRequest buildGeminiRequest(String prompt) {
        GeminiRequest request = new GeminiRequest();

        // Set content
        request.setContents(List.of(new GeminiRequest.Content(prompt)));

        // Set generation config
        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
        config.setTemperature(0.7);
        config.setMaxOutputTokens(300);
        config.setTopP(0.8);
        config.setTopK(40);
        request.setGenerationConfig(config);

        // Set safety settings
        request.setSafetySettings(List.of(
                new GeminiRequest.SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_MEDIUM_AND_ABOVE"),
                new GeminiRequest.SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"),
                new GeminiRequest.SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_MEDIUM_AND_ABOVE"),
                new GeminiRequest.SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_MEDIUM_AND_ABOVE")));

        return request;
    }
}