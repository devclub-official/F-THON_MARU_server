package com.maru.models;

import java.util.ArrayList;
import java.util.List;

public class GeminiChatResponse {
    private List<GeminiMessage> messages;
    
    public GeminiChatResponse() {
        this.messages = new ArrayList<>();
    }


    public GeminiChatResponse(List<GeminiMessage> messages) {
        this.messages = messages;
    }
    
    public List<GeminiMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<GeminiMessage> messages) {
        this.messages = messages;
    }
} 