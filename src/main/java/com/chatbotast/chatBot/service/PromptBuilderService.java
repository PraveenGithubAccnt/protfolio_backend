package com.chatbotast.chatBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromptBuilderService {

    @Autowired
    private PortfolioDataService portfolioDataService;

    private final List<String> conversationHistory = new ArrayList<>();
    private static final int MAX_HISTORY = 8; 
    public String buildPrompt(String userQuery) {
        String personName = portfolioDataService.getPersonName();
        
        StringBuilder prompt = new StringBuilder();
        
        
        prompt.append("You are an AI assistant for ").append(personName).append("'s professional portfolio. ");
        prompt.append("You have access to comprehensive information about him and should provide intelligent, conversational responses.\n\n");
        
        prompt.append("PORTFOLIO INFORMATION:\n\n");
        
        prompt.append("Personal Summary:\n");
        prompt.append(portfolioDataService.getDescription()).append("\n\n");
    
        prompt.append("Educational Background:\n");
        prompt.append(portfolioDataService.getEducation()).append("\n");
        
        prompt.append("Technical Skills:\n");
        prompt.append(portfolioDataService.getSkills()).append("\n");
        
        // Projects
        prompt.append("Key Projects:\n");
        prompt.append(portfolioDataService.getProjects()).append("\n");
        
        // Certifications
        prompt.append("Achievements & Certifications:\n");
        prompt.append(portfolioDataService.getCertifications()).append("\n");
        
        // Contact
        prompt.append("Contact Information:\n");
        prompt.append(portfolioDataService.getContact()).append("\n");
        
        // Conversation History
        if (!conversationHistory.isEmpty()) {
            prompt.append("Recent Conversation Context:\n");
            for (int i = 0; i < conversationHistory.size(); i++) {
                prompt.append(i % 2 == 0 ? "User: " : "You: ");
                prompt.append(conversationHistory.get(i)).append("\n");
            }
            prompt.append("\n");
        }
        
        // Response Instructions
        prompt.append("INSTRUCTIONS:\n");
        prompt.append("1. Respond as a knowledgeable AI assistant representing ").append(personName).append("\n");
        prompt.append("2. Be conversational, professional, and engaging\n");
        prompt.append("3. Use the provided information to give accurate, detailed responses\n");
        prompt.append("4. If asked about specific technical details not in the context, acknowledge limitations gracefully\n");
        prompt.append("5. Highlight his strengths, unique qualities, and professional achievements\n");
        prompt.append("6. Keep responses concise but informative (2-4 sentences typically)\n");
        prompt.append("7. Use a friendly, approachable tone while maintaining professionalism\n");
        prompt.append("8. Don't fabricate information not provided in the context\n");
        prompt.append("9. IMPORTANT: Only provide resume download link when specifically asked for 'resume', 'CV', or 'download'. ");
        prompt.append("Do NOT mention resume downloads in casual conversation\n");
        prompt.append("10. When users DO ask for resume download, provide this exact HTML: ");
        prompt.append("<a href=\"assets/resumepr.pdf\" download=\"Praveen_Resume.pdf\" class=\"resume-button\" ");
        prompt.append("style=\"display: inline-block; background-color: #007bff; color: white; padding: 8px 16px; ");
        prompt.append("text-decoration: none; border-radius: 5px; margin: 5px 0;\">📄 Download Resume</a>\n");
        prompt.append("11. When providing links (GitHub, LinkedIn, portfolio), always use the full URLs - ");
        prompt.append("these will be automatically converted to clickable links\n\n");
        
        // User Query
        prompt.append("Current User Question: ").append(userQuery).append("\n\n");
        prompt.append("Provide a helpful, intelligent response:");
        
        return prompt.toString();
    }

    public void addToHistory(String userMessage, String botResponse) {
        conversationHistory.add(userMessage);
        conversationHistory.add(botResponse);
        
        // Keep only recent history
        while (conversationHistory.size() > MAX_HISTORY * 2) {
            conversationHistory.remove(0);
            conversationHistory.remove(0);
        }
    }

    public void clearHistory() {
        conversationHistory.clear();
    }
}