package com.maru.services;

import com.maru.models.GeminiMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.IOException;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final String apiUrlPdf = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    
    public GeminiService(@Value("${spring.ai.vertex.ai.api-key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }
    
    public String generateContent(String prompt) {
        String url = apiUrl + "?key=" + apiKey;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String koreanPrompt = "다음 질문에 대해 한국어로 답변해 주세요: " + prompt;
        
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();
        
        parts.put("text", koreanPrompt);
        contents.put("parts", List.of(parts));
        requestBody.put("contents", List.of(contents));
        
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1024);
        requestBody.put("generationConfig", generationConfig);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates;
                
                // candidates가 List 또는 Map인지 확인하고 적절히 처리
                Object candidatesObj = response.get("candidates");
                if (candidatesObj instanceof List) {
                    candidates = (List<Map<String, Object>>) candidatesObj;
                } else if (candidatesObj instanceof Map) {
                    Map<String, Object> candidateMap = (Map<String, Object>) candidatesObj;
                    candidates = new ArrayList<>();
                    candidates.add(candidateMap);
                } else {
                    return "응답 형식이 예상과 다릅니다.";
                }
                
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    
                    if (candidate.containsKey("content")) {
                        Object contentObj = candidate.get("content");
                        Map<String, Object> content;
                        
                        // content가 List 또는 Map인지 확인하고 적절히 처리
                        if (contentObj instanceof List) {
                            List<Map<String, Object>> contentList = (List<Map<String, Object>>) contentObj;
                            if (!contentList.isEmpty()) {
                                content = contentList.get(0);
                            } else {
                                return "content 목록이 비어 있습니다.";
                            }
                        } else if (contentObj instanceof Map) {
                            content = (Map<String, Object>) contentObj;
                        } else {
                            return "content 형식이 예상과 다릅니다.";
                        }
                        
                        if (content.containsKey("parts")) {
                            Object partsObj = content.get("parts");
                            
                            // parts가 List 또는 Map인지 확인하고 적절히 처리
                            if (partsObj instanceof List) {
                                List<Map<String, Object>> partsList = (List<Map<String, Object>>) partsObj;
                                if (!partsList.isEmpty() && partsList.get(0).containsKey("text")) {
                                    return (String) partsList.get(0).get("text");
                                }
                            } else if (partsObj instanceof Map) {
                                Map<String, Object> partsMap = (Map<String, Object>) partsObj;
                                if (partsMap.containsKey("text")) {
                                    return (String) partsMap.get("text");
                                }
                            }
                        }
                    }
                }
            }
            
            // 디버깅을 위해 전체 응답 구조 출력
            return "응답 구조: " + response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "API 호출 중 오류 발생: " + e.getMessage();
        }
    }
    
    public String generateContentWithPdf(String prompt, MultipartFile pdfFile) {
        String url = apiUrlPdf + "?key=" + apiKey;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        try {
            // PDF 파일을 Base64로 인코딩
            byte[] pdfBytes = pdfFile.getBytes();
            String encodedPdf = Base64.getEncoder().encodeToString(pdfBytes);
            
            // 한국어 프롬프트 구성
            String koreanPrompt = "다음 질문에 대해 한국어로 답변해 주세요: " + prompt;
            
            // API 요청 본문 구성
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contentsList = new ArrayList<>();
            Map<String, Object> contents = new HashMap<>();
            List<Map<String, Object>> partsList = new ArrayList<>();
            
            // PDF 파일 파트 추가
            Map<String, Object> pdfPart = new HashMap<>();
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", "application/pdf");
            inlineData.put("data", encodedPdf);
            pdfPart.put("inline_data", inlineData);
            partsList.add(pdfPart);
            
            // 텍스트 프롬프트 파트 추가
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", koreanPrompt);
            partsList.add(textPart);
            
            contents.put("parts", partsList);
            contentsList.add(contents);
            requestBody.put("contents", contentsList);
            
            // 생성 설정 추가
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 1024);
            requestBody.put("generationConfig", generationConfig);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            // 응답 처리 (기존 generateContent 메서드와 동일한 로직)
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates;
                
                Object candidatesObj = response.get("candidates");
                if (candidatesObj instanceof List) {
                    candidates = (List<Map<String, Object>>) candidatesObj;
                } else if (candidatesObj instanceof Map) {
                    Map<String, Object> candidateMap = (Map<String, Object>) candidatesObj;
                    candidates = new ArrayList<>();
                    candidates.add(candidateMap);
                } else {
                    return "응답 형식이 예상과 다릅니다.";
                }
                
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    
                    if (candidate.containsKey("content")) {
                        Object contentObj = candidate.get("content");
                        Map<String, Object> content;
                        
                        if (contentObj instanceof List) {
                            List<Map<String, Object>> contentList = (List<Map<String, Object>>) contentObj;
                            if (!contentList.isEmpty()) {
                                content = contentList.get(0);
                            } else {
                                return "content 목록이 비어 있습니다.";
                            }
                        } else if (contentObj instanceof Map) {
                            content = (Map<String, Object>) contentObj;
                        } else {
                            return "content 형식이 예상과 다릅니다.";
                        }
                        
                        if (content.containsKey("parts")) {
                            Object partsObj = content.get("parts");
                            
                            if (partsObj instanceof List) {
                                List<Map<String, Object>> responsePartsList = (List<Map<String, Object>>) partsObj;
                                if (!responsePartsList.isEmpty() && responsePartsList.get(0).containsKey("text")) {
                                    return (String) responsePartsList.get(0).get("text");
                                }
                            } else if (partsObj instanceof Map) {
                                Map<String, Object> partsMap = (Map<String, Object>) partsObj;
                                if (partsMap.containsKey("text")) {
                                    return (String) partsMap.get("text");
                                }
                            }
                        }
                    }
                }
            }
            
            // 디버깅을 위해 전체 응답 구조 출력
            return "응답 구조: " + response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "PDF 파일 처리 중 오류 발생: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "API 호출 중 오류 발생: " + e.getMessage();
        }
    }
    
    // 대화 컨텍스트를 유지하는 메서드
    public Map.Entry<String, List<GeminiMessage>> generateChatContent(List<GeminiMessage> messages) {
        String url = apiUrl + "?key=" + apiKey;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contentsList = new ArrayList<>();
        
        // 메시지 목록을 API 요청 형식으로 변환
        for (GeminiMessage message : messages) {
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", message.getContent());
            
            String role = "user".equals(message.getRole()) ? "user" : "model";
            content.put("role", role);
            content.put("parts", List.of(part));
            contentsList.add(content);
        }
        
        requestBody.put("contents", contentsList);
        
        // 생성 설정 추가
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1024);
        requestBody.put("generationConfig", generationConfig);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            String responseText = extractTextFromResponse(response);
            
            // 새로운 응답을 메시지 목록에 추가
            List<GeminiMessage> updatedMessages = new ArrayList<>(messages);
            updatedMessages.add(new GeminiMessage("assistant", responseText));
            
            return Map.entry(responseText, updatedMessages);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "API 호출 중 오류 발생: " + e.getMessage();
            
            List<GeminiMessage> updatedMessages = new ArrayList<>(messages);
            updatedMessages.add(new GeminiMessage("assistant", errorMessage));
            
            return Map.entry(errorMessage, updatedMessages);
        }
    }
    
    // PDF와 대화 컨텍스트를 함께 처리하는 메서드
    public Map.Entry<String, List<GeminiMessage>> generateChatContentWithPdf(List<GeminiMessage> messages, MultipartFile pdfFile) {
        String url = apiUrlPdf + "?key=" + apiKey;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        try {
            // PDF 파일을 Base64로 인코딩
            byte[] pdfBytes = pdfFile.getBytes();
            String encodedPdf = Base64.getEncoder().encodeToString(pdfBytes);
            
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contentsList = new ArrayList<>();
            
            // 첫 번째 메시지에 PDF를 첨부
            if (!messages.isEmpty()) {
                Map<String, Object> firstContent = new HashMap<>();
                List<Map<String, Object>> firstPartsList = new ArrayList<>();
                
                // PDF 파일 파트 추가
                Map<String, Object> pdfPart = new HashMap<>();
                Map<String, Object> inlineData = new HashMap<>();
                inlineData.put("mime_type", "application/pdf");
                inlineData.put("data", encodedPdf);
                pdfPart.put("inline_data", inlineData);
                firstPartsList.add(pdfPart);
                
                // 첫 번째 메시지의 텍스트 추가
                Map<String, Object> textPart = new HashMap<>();
                textPart.put("text", messages.get(0).getContent());
                firstPartsList.add(textPart);
                
                String role = "user".equals(messages.get(0).getRole()) ? "user" : "model";
                firstContent.put("role", role);
                firstContent.put("parts", firstPartsList);
                contentsList.add(firstContent);
                
                // 나머지 메시지 추가
                for (int i = 1; i < messages.size(); i++) {
                    GeminiMessage message = messages.get(i);
                    Map<String, Object> content = new HashMap<>();
                    Map<String, Object> part = new HashMap<>();
                    part.put("text", message.getContent());
                    
                    role = "user".equals(message.getRole()) ? "user" : "model";
                    content.put("role", role);
                    content.put("parts", List.of(part));
                    contentsList.add(content);
                }
            }
            
            requestBody.put("contents", contentsList);
            
            // 생성 설정 추가
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 1024);
            requestBody.put("generationConfig", generationConfig);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            
            String responseText = extractTextFromResponse(response);
            
            // 새로운 응답을 메시지 목록에 추가
            List<GeminiMessage> updatedMessages = new ArrayList<>(messages);
            updatedMessages.add(new GeminiMessage("assistant", responseText));
            
            return Map.entry(responseText, updatedMessages);
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = "PDF 파일 처리 중 오류 발생: " + e.getMessage();
            
            List<GeminiMessage> updatedMessages = new ArrayList<>(messages);
            updatedMessages.add(new GeminiMessage("assistant", errorMessage));
            
            return Map.entry(errorMessage, updatedMessages);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "API 호출 중 오류 발생: " + e.getMessage();
            
            List<GeminiMessage> updatedMessages = new ArrayList<>(messages);
            updatedMessages.add(new GeminiMessage("assistant", errorMessage));
            
            return Map.entry(errorMessage, updatedMessages);
        }
    }
    
    // 응답에서 텍스트를 추출하는 헬퍼 메서드
    private String extractTextFromResponse(Map<String, Object> response) {
        if (response != null && response.containsKey("candidates")) {
            List<Map<String, Object>> candidates;
            
            Object candidatesObj = response.get("candidates");
            if (candidatesObj instanceof List) {
                candidates = (List<Map<String, Object>>) candidatesObj;
            } else if (candidatesObj instanceof Map) {
                Map<String, Object> candidateMap = (Map<String, Object>) candidatesObj;
                candidates = new ArrayList<>();
                candidates.add(candidateMap);
            } else {
                return "응답 형식이 예상과 다릅니다.";
            }
            
            if (!candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                
                if (candidate.containsKey("content")) {
                    Object contentObj = candidate.get("content");
                    Map<String, Object> content;
                    
                    if (contentObj instanceof List) {
                        List<Map<String, Object>> contentList = (List<Map<String, Object>>) contentObj;
                        if (!contentList.isEmpty()) {
                            content = contentList.get(0);
                        } else {
                            return "content 목록이 비어 있습니다.";
                        }
                    } else if (contentObj instanceof Map) {
                        content = (Map<String, Object>) contentObj;
                    } else {
                        return "content 형식이 예상과 다릅니다.";
                    }
                    
                    if (content.containsKey("parts")) {
                        Object partsObj = content.get("parts");
                        
                        if (partsObj instanceof List) {
                            List<Map<String, Object>> partsList = (List<Map<String, Object>>) partsObj;
                            if (!partsList.isEmpty() && partsList.get(0).containsKey("text")) {
                                return (String) partsList.get(0).get("text");
                            }
                        } else if (partsObj instanceof Map) {
                            Map<String, Object> partsMap = (Map<String, Object>) partsObj;
                            if (partsMap.containsKey("text")) {
                                return (String) partsMap.get("text");
                            }
                        }
                    }
                }
            }
        }
        
        // 디버깅을 위해 전체 응답 구조 출력
        return "응답 구조: " + response.toString();
    }
} 