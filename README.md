# F-THON_MARU_server

**함께 정상까지**  

F-THON_MARU_server는 **AI 기반 이력서 분석 및 기술 멘토링 시스템**입니다.  
사용자의 PDF 이력서를 분석하고 기술 역량을 평가하여 맞춤형 질문 및 피드백을 제공합니다.  
Google Gemini API를 활용해 대화형 기술 평가와 학습 방향을 제시합니다.

---

## 🔍 프로젝트 개요
**F-THON_MARU_server**는 구직자의 이력서를 AI로 분석하고, 대화형 기술 인터뷰를 통해 기술 스택 기반의 평가 및 피드백을 제공합니다.  

---

## ⚙️ 핵심 기능

- 📄 **이력서 PDF 분석**: 사용자의 PDF 이력서에서 기술 스택 추출
- 🧠 **기술 역량 평가**: 기술 스택에 맞춘 맞춤형 질문 생성 및 답변 분석
- 💬 **대화형 멘토링**: 사용자와 AI 간 자연스러운 기술 인터뷰 진행
- 📈 **맞춤형 피드백 제공**: 기술 수준에 따른 학습 방향 제시
- 📊 **구조화된 리포트 생성**: JSON 형식으로 기술별 평가 및 개선 방향 제공

---

## 🛠️ 기술적 특징

### 1. PDF 처리 및 Base64 인코딩
- **Base64 사용 이유**:
  - 바이너리 파일을 HTTP JSON 요청으로 전송 가능
  - 웹/모바일 환경 호환성 확보
  - Google Gemini API 멀티모달 요구사항 대응

- **구현 방식**:
  - `Base64DecodedMultipartFile` 클래스로 Base64 문자열을 `MultipartFile`로 변환
  - 프론트엔드와 JSON 기반 통신 유지

### 2. 대화 컨텍스트 유지
- `List<GeminiMessage>` 형태로 전체 대화 히스토리 유지
- 첫 메시지에 PDF 첨부 → 이후 대화에서도 해당 정보 재활용 가능
- 역할 구분 구조: `user`, `model`(or `assistant`), `system`

### 3. Google Gemini API 통합
- `RestTemplate`을 통한 직접 API 호출
- 텍스트 + PDF를 포함한 **멀티모달 요청 구성**
- 다양한 응답 형식에 대응하는 **재귀 파싱 로직**

### 4. 기술 역량 평가 알고리즘
- 답변의 질에 따라 **질문 난이도 자동 조절**
- 기술 수준 기반 **개인화된 피드백 제공**
- **구조화된 JSON 리포트**로 평가 결과 제공

---

## 🧱 개발 환경 및 설정

### Prerequisites
- Java 17+
- Maven 3.8+

### 설치 및 실행

```bash
git clone <repository-url>
cd F-THON_MARU_server
mvn clean install
./mvnw spring-boot:run
