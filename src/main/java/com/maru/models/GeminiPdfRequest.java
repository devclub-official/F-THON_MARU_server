package com.maru.models;

import org.springframework.web.multipart.MultipartFile;

public class GeminiPdfRequest {
    private String prompt;
    private MultipartFile pdfFile;

    public GeminiPdfRequest() {
    }

    public GeminiPdfRequest(String prompt, MultipartFile pdfFile) {
        this.prompt = prompt;
        this.pdfFile = pdfFile;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public MultipartFile getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(MultipartFile pdfFile) {
        this.pdfFile = pdfFile;
    }
} 