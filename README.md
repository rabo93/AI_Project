# ChatGPT API를 활용한 웹 기반 챗봇 서비스 개발

1. 기획 의도
- 생성형 AI - 대규모 언어 모델(LLM) 서비스 구현해보기
- API 활용 - LLM API (Open AI)를 활용, pdf 텍스트 추출(Apache PDFBox 활용), 사용자 요청 보내기-응답 처리 등 기술 익히기
- 프롬프트 엔지니어링 - LLM 서비스의 기능은 프롬프트에 크게 좌우됨. 결과를 얻기 위해 다양한 프롬프트를 시도하고 개선 해보기

2. 주제 
- 간단한 Q&A 챗봇
    - 자유 질문/답변 기능
    - 특정 주제 pdf 문서를 넣으면 그걸 기반으로 질문을 받고 답하는 챗봇
    - 기술 포인트 : LangChain 연동
    - 작은 데이터로도 가능
    - 문서기반 챗봇 : LangChain + 벡터DB + LLM API 결합시 실용적이고 구조 명확

- 영어로 번역 (개발시 클래스 및 메서드명 영문명으로 작성할때 적절한 영문명으로 변환)    
⇒ 위 주제 다하고 구현해보기

3. 개발환경 및 구성도
- SpringBoot 서버  + Thymeleaf  프론트
- Open AI API + 문서 처리( LangChain 또는 직접 구현)
- MySQL + 벡터 검색(Faiss, Chroma)


4. 주요 기능 구성
| 기능 | 설명 |
| --- | --- |
| 문서 업로드 | PDF 또는 텍스트 문서를 업로드 |
| 문서 파싱 | PDF → 텍스트 추출 후 저장 |
| 벡터화 | 문서를 Chunk로 나누고 임베딩 생성 |
| 유사도 검색 | 질문 입력 시 관련 Chunk 검색 |
| LLM 응답 | 검색된 내용을 기반으로 GPT에게 응답 생성 요청 |
| 답변 출력 | 사용자에게 자연스러운 Q&A 형태로 출력 |

5. 기술 포인트
| 기능 | 설명 |
| --- | --- |
| 문서 처리 | Apache PDFBox으로 PDF 파싱 |
| 벡터화 | OpenAI Embedding API 또는 HuggingFace 사용 |
| 검색 | ChromaDB 또는 Faiss를 Java에서 연동 가능 (파일 기반으로도 가능) |
| LLM 호출 | OpenAI API 사용 ( WebClient로 호출) |
| DB 저장 | 문서, 질문 기록 등을 MySql에 저장 |

6. 이후 더 발전해나갈 서비스 방향
- PDF 기반 문서 검색/질의응답 서비스로 확장
    - 단순 요약을 넘어서 사용자 질문에 대해 문서 내에서 관련 내용을 찾아 응답하는 기능
    - ex) “이 계약서에서 위약금 조항은 뭐야?” → 해당 조항을 찾아 요약해 응답
- 다양한 파일 포맷 지원
    - .docx, .txt, .html, .xlsx 등 다양한 문서 포맷 처리
- 멀티 문서 통합 요약 및 비교
  - 여러 문서를 한 번에 업로드해 통합 요약 또는 비교 분석
  - ex) 보고서 A와 B의 주요 차이점 요약
- 사용자별 문서 관리 시스템
    - 로그인 기능 추가
    - 개인 문서 업로드 이력, 요약 히스토리 저장/조회
 - PDF 내 표, 그래프, 이미지 이해 및 변환


---

구성

src/main/java

    ㄴ com.winbit.project
        ㄴ config
            ㄴ WebClientConfig.java
        ㄴ controller
            ㄴ ChatbotController.java
        ㄴ dto
            ㄴ ChatMessage.java
        ㄴ service
            ㄴ OpenAIService.java
        ㄴ util
            ㄴ MarkdownUtil.java
        ㄴ DocumentChatbotApplication.java

src/main/resources

    ㄴ templates
        ㄴ chatbot.html
        ㄴ index.html
        ㄴ pdf_summary.html

---

![image](https://github.com/user-attachments/assets/bc8164d3-f7c3-4ebb-a4d6-361a8e62e04d)
![image](https://github.com/user-attachments/assets/c637b39a-af69-4249-9f22-cabc05796491)
![image](https://github.com/user-attachments/assets/89edc60a-d37c-4160-8261-b2f0afb65d7f)


