// package com.maru.controllers;

// import com.maru.models.AiResponse;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.ai.chat.messages.UserMessage;
// import org.springframework.ai.chat.model.ChatResponse;
// import org.springframework.ai.chat.prompt.Prompt;
// import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import reactor.core.publisher.Flux;

// import java.util.Map;

// @RestController
// @RequestMapping("/api/ai")
// @Tag(name = "AI", description = "AI 응답 생성 API")
// public class AiController {

//     private static final Logger logger = LoggerFactory.getLogger(AiController.class);
//     private final VertexAiGeminiChatModel chatModel;

//     @Autowired
//     public AiController(VertexAiGeminiChatModel chatModel) {
//         this.chatModel = chatModel;
//     }

//     @Operation(
//             summary = "AI 응답 생성",
//             description = "사용자 프롬프트에 대한 AI 응답을 생성합니다.",
//             responses = {
//                     @ApiResponse(
//                             responseCode = "200",
//                             description = "응답 생성 성공",
//                             content = @Content(mediaType = "application/json",
//                                     schema = @Schema(implementation = AiResponse.class))
//                     ),
//                     @ApiResponse(
//                             responseCode = "500",
//                             description = "서버 오류",
//                             content = @Content(mediaType = "application/json",
//                                     schema = @Schema(implementation = AiResponse.class))
//                     )
//             }
//     )

//     @GetMapping("/ai/generate")
//     public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//         return Map.of("generation", this.chatModel.call(message));
//     }

//     @GetMapping("/ai/generateStream")
//     public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//         Prompt prompt = new Prompt(new UserMessage(message));
//         return this.chatModel.stream(prompt);
//     }

// //    @PostMapping("/chat")
// //    public ResponseEntity<AiResponse> generateResponse(@RequestBody AiRequest request) {
// //
// //        try {
// //            logger.info("AI 요청 수신: {}", request.getPrompt());
// //            long startTime = System.currentTimeMillis();
// //            String response = aiService.generateResponse(request.getPrompt());
// //            long endTime = System.currentTimeMillis();
// //            long processingTime = endTime - startTime;
// //
// //            logger.info("AI 응답 생성 완료 (처리 시간: {}ms)", processingTime);
// //            AiResponse aiResponse = new AiResponse(
// //                response,
// //                "success",
// //                processingTime
// //            );
// //
// //            return ResponseEntity.ok(aiResponse);
// //        } catch (Exception e) {
// //            logger.error("AI 응답 생성 중 오류 발생", e);
// //            AiResponse errorResponse = new AiResponse(
// //                "AI 서비스 처리 중 오류가 발생했습니다: " + e.getMessage(),
// //                "error",
// //                0
// //            );
// //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
// //        }
// }