package com.maru.models;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class GeminiChatPdfRequest {
    private List<GeminiMessage> messages;
    private MultipartFile pdfFile;
    
    public GeminiChatPdfRequest() {
        this.messages = new ArrayList<>();
    }
    
    public GeminiChatPdfRequest(List<GeminiMessage> messages, MultipartFile pdfFile) {
        this.messages = messages;
        this.pdfFile = pdfFile;
    }
    
    public List<GeminiMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<GeminiMessage> messages) {
        this.messages = messages;
    }
    
    public MultipartFile getPdfFile() {
        return pdfFile;
    }
    
    public void setPdfFile(MultipartFile pdfFile) {
        this.pdfFile = pdfFile;
    }
    
    public void addMessage(String role, String content) {
        this.messages.add(new GeminiMessage(role, content));
    }
} 