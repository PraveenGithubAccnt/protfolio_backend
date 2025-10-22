package com.chatbotast.chatBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PortfolioDataService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioDataService.class);
    private JsonNode portfolioData;

    @PostConstruct
    public void loadPortfolioData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("personal_data.json");
            InputStream inputStream = resource.getInputStream();
            portfolioData = mapper.readTree(inputStream);
            logger.info("Successfully loaded portfolio data");
        } catch (IOException e) {
            logger.error("Failed to load portfolio data", e);
            portfolioData = null;
        }
    }

    public String getPersonName() {
        if (portfolioData == null) return "Praveen Kumar Kushwaha";
        return portfolioData.path("summary").path("name").asText("Praveen Kumar Kushwaha");
    }

    public String getDescription() {
        if (portfolioData == null) return "";
        return portfolioData.path("summary").path("description").asText("");
    }

    public String getEducation() {
        if (portfolioData == null) return "";
        StringBuilder education = new StringBuilder();
        JsonNode eduArray = portfolioData.path("education");
        eduArray.forEach(edu -> {
            education.append("- ")
                    .append(edu.path("degree").asText())
                    .append(" from ")
                    .append(edu.path("institution").asText())
                    .append(" (Graduated: ")
                    .append(edu.path("graduation_date").asText())
                    .append(")\n");
        });
        return education.toString();
    }

    public String getSkills() {
        if (portfolioData == null) return "";
        StringBuilder skills = new StringBuilder();
        JsonNode skillsNode = portfolioData.path("skills");
        
        skills.append("- Programming Languages: ")
              .append(getArrayAsString(skillsNode.path("languages"))).append("\n");
        skills.append("- Frameworks & Libraries: ")
              .append(getArrayAsString(skillsNode.path("frameworks_libraries"))).append("\n");
        skills.append("- AI & Machine Learning: ")
              .append(getArrayAsString(skillsNode.path("ai_ml"))).append("\n");
        skills.append("- Development Tools: ")
              .append(getArrayAsString(skillsNode.path("tools_platforms"))).append("\n");
        skills.append("- Core Concepts: ")
              .append(getArrayAsString(skillsNode.path("core_concepts"))).append("\n");
        skills.append("- Soft Skills: ")
              .append(getArrayAsString(skillsNode.path("soft_skills"))).append("\n");
        
        return skills.toString();
    }

    public String getProjects() {
        if (portfolioData == null) return "";
        StringBuilder projects = new StringBuilder();
        JsonNode projectsArray = portfolioData.path("projects");
        
        projectsArray.forEach(proj -> {
            projects.append("Project: ").append(proj.path("title").asText()).append("\n");
            projects.append("   Timeline: ").append(proj.path("timeline").asText()).append("\n");
            projects.append("   Technologies: ").append(proj.path("technologies").asText()).append("\n");
            projects.append("   Description: ").append(proj.path("description").asText()).append("\n");
            if (proj.has("link") && !proj.path("link").asText().isEmpty()) {
                projects.append("   Link: ").append(proj.path("link").asText()).append("\n");
            }
            projects.append("\n");
        });
        
        return projects.toString();
    }

    public String getCertifications() {
        if (portfolioData == null) return "";
        StringBuilder certs = new StringBuilder();
        JsonNode certsArray = portfolioData.path("certifications_honors");
        
        certsArray.forEach(cert -> {
            certs.append("- ").append(cert.asText()).append("\n");
        });
        
        return certs.toString();
    }

    public String getContact() {
        if (portfolioData == null) return "";
        JsonNode contact = portfolioData.path("contact");
        StringBuilder contactInfo = new StringBuilder();
        
        contactInfo.append("- Email: ").append(contact.path("email").asText()).append("\n");
        contactInfo.append("- Phone: ").append(contact.path("phone").asText()).append("\n");
        contactInfo.append("- Location: ").append(contact.path("address").asText()).append("\n");
        contactInfo.append("- LinkedIn: ").append(contact.path("linkedin").asText()).append("\n");
        contactInfo.append("- GitHub: ").append(contact.path("github").asText()).append("\n");
        contactInfo.append("- Portfolio: ").append(contact.path("portfolio").asText()).append("\n");
        
        return contactInfo.toString();
    }

    private String getArrayAsString(JsonNode arrayNode) {
        StringBuilder result = new StringBuilder();
        if (arrayNode.isArray()) {
            for (int i = 0; i < arrayNode.size(); i++) {
                result.append(arrayNode.get(i).asText());
                if (i < arrayNode.size() - 1) {
                    result.append(", ");
                }
            }
        }
        return result.toString();
    }

    public JsonNode getPortfolioData() {
        return portfolioData;
    }
}