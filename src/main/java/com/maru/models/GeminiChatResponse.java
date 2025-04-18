package com.maru.models;

import java.util.ArrayList;
import java.util.List;

public class GeminiChatResponse {
    private String response;
    private List<GeminiMessage> messages;
    
    public GeminiChatResponse() {
        this.messages = new ArrayList<>();
    }
    
    public GeminiChatResponse(String response) {
        this.response = response;
        this.messages = new ArrayList<>();
    }
    
    public GeminiChatResponse(String response, List<GeminiMessage> messages) {
        this.response = response;
        this.messages = messages;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public List<GeminiMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<GeminiMessage> messages) {
        this.messages = messages;
    }
} 