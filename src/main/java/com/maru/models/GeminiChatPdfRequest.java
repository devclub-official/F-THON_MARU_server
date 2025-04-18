package com.maru.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class GeminiChatPdfRequest {
    private List<GeminiMessage> messages;
    
    @JsonIgnore
    private MultipartFile pdfFile;
    
    @JsonProperty("pdfFileData")
    private PdfFileData pdfFileData;
    
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
        if (pdfFile == null && pdfFileData != null && pdfFileData.getContent() != null) {
            return Base64DecodedMultipartFile.base64ToMultipart(
                    pdfFileData.getContent(),
                    pdfFileData.getName() != null ? pdfFileData.getName() : "file.pdf",
                    "application/pdf"
            );
        }
        return pdfFile;
    }
    
    public void setPdfFile(MultipartFile pdfFile) {
        this.pdfFile = pdfFile;
    }
    
    public PdfFileData getPdfFileData() {
        return pdfFileData;
    }
    
    public void setPdfFileData(PdfFileData pdfFileData) {
        this.pdfFileData = pdfFileData;
    }
    
    public void addMessage(String role, String content) {
        this.messages.add(new GeminiMessage(role, content));
    }
    
    public static class PdfFileData {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("content")
        private String content;
        
        public PdfFileData() {
        }
        
        public PdfFileData(String name, String content) {
            this.name = name;
            this.content = content;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
} 