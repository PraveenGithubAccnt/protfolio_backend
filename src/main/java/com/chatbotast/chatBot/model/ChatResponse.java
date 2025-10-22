
package com.chatbotast.chatBot.model;

public class ChatResponse {
    private String response;
    private String error;

    public ChatResponse() {}

    public ChatResponse(String response) {
        this.response = response;
    }

    public static ChatResponse error(String error) {
        ChatResponse response = new ChatResponse();
        response.setError(error);
        return response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}