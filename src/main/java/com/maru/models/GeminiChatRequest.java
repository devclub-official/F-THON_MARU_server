package com.maru.models;

import java.util.ArrayList;
import java.util.List;

public class GeminiChatRequest {
    private List<GeminiMessage> messages;
    
    public GeminiChatRequest() {
        this.messages = new ArrayList<>();
    }
    
    public GeminiChatRequest(List<GeminiMessage> messages) {
        this.messages = messages;
    }
    
    public List<GeminiMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<GeminiMessage> messages) {
        this.messages = messages;
    }
    
    public void addMessage(String role, String content) {
        this.messages.add(new GeminiMessage(role, content));
    }
} 