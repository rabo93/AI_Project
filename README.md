# AI_Project

1. 기획 의도
- 생성형 AI - 대규모 언어 모델(LLM) 서비스 구현해보기
- API 활용 - LLM API (Open AI, Google gemini, Meta Llama3 등)를 활용, API 문서 읽기, 요청 보내기, 응답 처리 등 기술 익히기
- 프롬프트 엔지니어링 - LLM 서비스의 기능은 프롬프트에 크게 좌우됨. 결과를 얻기 위해 다양한 프롬프트를 시도하고 개선 해보기

1. 주제 
- 간단한 Q&A 챗봇
    - 특정 주제 pdf나 faq 문서를 넣으면 그걸 기반으로 질문을 받고 답하는 챗봇
    - 기술 포인트 : LangChain 연동
    - 작은 데이터로도 가능
    - 문서기반 챗봇 : LangChain + 벡터DB + LLM API 결합시 실용적이고 구조 명확

- 영어로 번역 (개발시 클래스 및 메서드명 영문명으로 작성할때 적절한 영문명으로 변환)
    
    ⇒ 위 주제 다하고 구현해보기
    

1. 개발환경 및 구성도
- SpringBoot 서버  + Thymeleaf  프론트
- Open AI API + 문서 처리( LangChain 또는 직접 구현)
- MySQL + 벡터 검색(Faiss, Chroma)

1. 주요 기능 구성

| 기능 | 설명 |
| --- | --- |
| 문서 업로드 | PDF 또는 텍스트 문서를 업로드 |
| 문서 파싱 | PDF → 텍스트 추출 후 저장 |
| 벡터화 | 문서를 Chunk로 나누고 임베딩 생성 |
| 유사도 검색 | 질문 입력 시 관련 Chunk 검색 |
| LLM 응답 | 검색된 내용을 기반으로 GPT에게 응답 생성 요청 |
| 답변 출력 | 사용자에게 자연스러운 Q&A 형태로 출력 |

1. 기술 포인트

| 기능 | 설명 |
| --- | --- |
| 문서 처리 | Apache PDFBox, Tika 등으로 PDF 파싱 |
| 벡터화 | OpenAI Embedding API 또는 HuggingFace 사용 |
| 검색 | ChromaDB 또는 Faiss를 Java에서 연동 가능 (파일 기반으로도 가능) |
| LLM 호출 | OpenAI API 사용 ( WebClient로 호출) |
| DB 저장 | 문서, 질문 기록 등을 MySql에 저장 |

1. 기술 개발 순서 추천
    - 기본 페이지 구성 (Thymeleaf + SpringBoot)
    - OpenAI API 연동 → 질문하면 답변 나오게 하기
    - 문서 업로드 기능 추가
    - 문서 임베딩 및 벡터 검색 구현
    - 질문 기반 유사 문서 검색 → 답변 생성 흐름 구축
    - (선택) 질문 로그 저장, 문서 관리 페이지 등 확장

---

구성

src/main/java

    ㄴ com.winbit.project

        ㄴ config

            ㄴ WebClientConfig.java

    ㄴ controller

        ㄴ ChatbotController.java

    ㄴ service

        ㄴ OpenAIService.java

    ㄴ DocumentChatbotApplication.java

src/main/resources

    ㄴ templates

        ㄴ chatbot.html
