
package com.chatbotast.chatBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatBotApplication {  

    public static void main(String[] args) {
        SpringApplication.run(ChatBotApplication.class, args);
        
        // Startup message to confirm server is running
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   🤖 ChatBot Backend Started Successfully");
        System.out.println("╚══════════════════════════════════════════╝");
     
      
    }
}