package com.maru.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maru.models.*;
import com.maru.services.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
@Tag(name = "Gemini", description = "Google Gemini API 관련 기능")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    /**
     * PDF 파일을 JSON 형태로 받는 엔드포인트
     */
    @Operation(
        summary = "PDF 분석 및 대화 컨텍스트 유지 응답 생성 (JSON)",
        description = "PDF 파일을 JSON 형식으로 받아 분석하고 Gemini API를 통해 대화 컨텍스트를 유지하며 한국어로 텍스트를 생성합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "요청 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = GeminiChatResponse.class))
            )
        }
    )
    @PostMapping(value = "/chat-with-pdf-json", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GeminiChatResponse generateChatContentWithPdfJson(@RequestBody GeminiChatPdfRequest request) {
        
        List<GeminiMessage> messages = request.getMessages();
        
        // 메시지가 없으면 기본 메시지 추가
        if (messages == null || messages.isEmpty()) {
            messages = new ArrayList<>();
            messages.add(new GeminiMessage("user", "너는 내 기술 멘토야.\n" +
                    "\n" +
                    "내 이력서에서 기술 스택을 랜덤하게 하나씩 선택해.\n" +
                    "\n" +
                    "각 기술별로 실력 점검 질문을 한 번에 하나씩만 해줘.\n" +
                    "\n" +
                    "내가 답변하면, 정답과 상관없이 반드시 해설, 평가, 정답, 설명, 피드백, 요약 등은 절대 출력하지 마. 반드시 꼬리질문(또는 다음 질문)만 출력해줘.\n" +
                    "\n" +
                    "내가 한 답변이 질문에 대한 정답과 정합성이 60% 이하이면 동일 skill의 다른 질문으로 바꿔서 해줘.\n" +
                    "\n" +
                    "내 답변의 정답률이 40% 미만이라고 판단되면, 난이도를 낮춰서 꼬리질문을 해줘.\n" +
                    "\n" +
                    "질문은 기본 → 심화 순서로 진행해줘. 기본의 기준은 정보처리기사 수준이야.\n" +
                    "\n" +
                    "내가 '다음'이라고 하면 다음 기술로 넘어가고, '멈춰'라고 하면 종료해.\n" +
                    "\n" +
                    "'멈춰'를 통해 대화가 종료 되면 위의 질문들과 나의 모든 답변, 그리고 멘토의 모든 질문과 답변을 포함하여 내 기술 수준에 대한 현재 상태 및 공부 방향, 틀린 질문과 정답 질문을 제시해주는 리포트를 생성해서 줘.\n" +
                    "\n" +
                    "특히 리포트 내용에서 각 평가 대상 기술과 해당 기술의 현재 상태에 대한 피드백과 개선 공부 방향을 세부적으로 최대한 자세하게 분석하여 아래의 json형식으로 전달해줘. 개선을 위한 공부 방향에 대해서는 참조할만한 링크도 같이 전달해줘.\n" +
                    "\n" +
                    "답변 시 '알겠습니다'와 같은 확인 응답은 생략하고 질문만 해줘.\n" +
                    "\n" +
                    "리포트 json 형식 - {\n" +
                    "\n" +
                    "\"summary\": 현재 상태 요약,\n" +
                    "\n" +
                    "\"feedback\": 현재 상태 관련 상세 피드백,\n" +
                    "\n" +
                    "\"teck_skills_analysis\" : [\n" +
                    "\n" +
                    "{\n" +
                    "\n" +
                    "\"tech_skill\": 기술,\n" +
                    "\n" +
                    "\"current_state\": 현재 기술 수준에 대한 설명,\n" +
                    "\n" +
                    "\"imporovement_direction\" : 향후 개선 방향,\n" +
                    "\n" +
                    "\"reference_links\": 참고할만한 링크 리스트,\n" +
                    "\n" +
                    "}\n" +
                    "\n" +
                    "],\n" +
                    "\n" +
                    "\"correct_questions\": [\n" +
                    "\n" +
                    "{\n" +
                    "\n" +
                    "\"tech_skill\": 기술명,\n" +
                    "\n" +
                    "\"question\": 맞힌 질문,\n" +
                    "\n" +
                    "\"your_answer\": 내가 제출한 답,\n" +
                    "\n" +
                    "},\n" +
                    "\n" +
                    "\"incorrect_questions\": [\n" +
                    "\n" +
                    "{\n" +
                    "\n" +
                    "\"tech_skill\": 기술명,\n" +
                    "\n" +
                    "\"question\": 틀린 질문,\n" +
                    "\n" +
                    "\"your_answer\": 내가 제출한 답,\n" +
                    "\n" +
                    "\"right_answer\": 정답\n" +
                    "\n" +
                    "}\n" +
                    "]\n" +
                    "}"));
        }
        
        MultipartFile pdfFile = request.getPdfFile();
        
        // PDF 파일이 없으면 오류 응답 반환
        if (pdfFile == null || pdfFile.isEmpty()) {
            String errorMessage = "PDF 파일이 제공되지 않았습니다. 'pdfFile' 또는 'pdfFileData' 필드를 통해 PDF 파일을 제공해주세요.";
            System.out.println("Error: " + errorMessage);
            return new GeminiChatResponse(errorMessage, messages);
        }
        
        // MultipartFile 정보 출력
        try {
            System.out.println("PDF File name: " + pdfFile.getOriginalFilename());
            System.out.println("PDF File size: " + pdfFile.getSize());
            System.out.println("PDF File content type: " + pdfFile.getContentType());
        } catch (Exception e) {
            System.out.println("Error getting PDF file info: " + e.getMessage());
        }
        
        Map.Entry<String, List<GeminiMessage>> result = geminiService.generateChatContentWithPdf(messages, pdfFile);
        return new GeminiChatResponse(result.getKey(), result.getValue());
    }

} 