package com.winbit.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winbit.project.util.MarkdownUtil;

import reactor.core.publisher.Mono;

@Service
public class OpenAIService {
	@Autowired
	private WebClient openAIWebClient;
	
	@Value("${openai.model}")
	private String model;
	
	// 사용자 메시지를 받아 OpenAI API에 요청하고 응답을 반환하는 메서드
	// WebClient는 기본적으로 비동기이기 때문에 getChatAnswer()의 반환형이 Mono<String>!!
	public Mono<String> getChatAnswer(String question) {
		
		// 요청 본문 생성 (Chat Completion 포맷에 맞게 구성)
		Map<String, Object> requestBody = Map.of(
				"model", model, //gpt-3.5-turbo
				"messages", List.of(
							// 시스템 메세지 : 챗봇 역할 정의
							Map.of("role", "developer", "content", "You are a helpful assistant."),
							// 사용자 메세지 : 실제 유저의 질문
							Map.of("role", "user", "content", question)
						)
				);
		System.out.println("requestBody : " + requestBody);
		//requestBody : {message=[{role=system, content=You are a helpful assistant.}, {role=system, content=질문 시작해도 돼요?}], model=gpt-4.1}
		
		// WebClient를 통해 POST 요청을 전송하고 응답을 Mono<String>으로 받음
		return openAIWebClient.post()
				.bodyValue(requestBody) // 요청 본문 설정
				.retrieve() // 응답을 가져옴
				.onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, response -> {
				    return Mono.error(new RuntimeException("요청이 너무 많습니다. 잠시 후 다시 시도해주세요."));
				})
				.bodyToMono(String.class) // 응답을 문자열(JSON 형식)로 변환
				.map(this::extraContentFromResponse); // 응답에서 'content'만 추출
		
		
		// responseBody 전체 확인할 때
//		return openAIWebClient.post()
//		        .bodyValue(requestBody)
//		        .exchangeToMono(response -> {
//		            if (response.statusCode().isError()) {
//		                System.out.println("Error response from OpenAI: " + response.statusCode());
//		                return Mono.error(new RuntimeException("OpenAI API error"));
//		            }
//		            return response.bodyToMono(String.class);
//		        });
		
		/*
		 {
			  "id": "chatcmpl-BPQ5XIc9ruZRzACi0KUcRiGwur5PP",
			  "object": "chat.completion",
			  "created": 1745398183,
			  "model": "gpt-4o-2024-08-06",
			  "choices": [
			    {
			      "index": 0,
			      "message": {
			        "role": "assistant",
			        "content": "어떤 문서든지 다양한 질문을 할 수 있어요. 예를 들어, 문서의 주제나 목적에 대해 묻거나, 주요 내용을 요약해 보라고 요청할 수 있습니다. 또는 문서의 특정 부분에 대한 더 깊은 이해가 필요하다면, 해당 부분에 대해 질문을 할 수 있죠. 예를 들어:\n\n1. 문서의 주요 목적은 무엇인가요?\n2. 이 문서에서 가장 중요한 세 가지 포인트는 무엇인가요?\n3. 특정 문장이 어떤 의미인지 설명해 주실 수 있나요?\n4. 이 문서가 어떤 문제를 해결하려고 하나요?\n5. 문서에서 제안하는 해결책은 무엇인가요?\n\n이렇게 구체적인 부분이나 전체적인 내용을 파악하는 데 유용한 질문을 해볼 수 있습니다. 도움 필요하시면 구체적인 문서에 대해서 더 말씀해 주세요!",
			        // => content만 따로 추출해야한다!!!!!!!!!!!
			        "refusal": null,
			        "annotations": []
			      },
			      "logprobs": null,
			      "finish_reason": "stop"
			    }
			  ],
			  "usage": {
			    "prompt_tokens": 25,
			    "completion_tokens": 191,
			    "total_tokens": 216,
			    "prompt_tokens_details": {
			      "cached_tokens": 0,
			      "audio_tokens": 0
			    },
			    "completion_tokens_details": {
			      "reasoning_tokens": 0,
			      "audio_tokens": 0,
			      "accepted_prediction_tokens": 0,
			      "rejected_prediction_tokens": 0
			    }
			  },
			  "service_tier": "default",
			  "system_fingerprint": "fp_f5bdcc3276"
			}
		 * */

	}
	
	// OpenAI의 JSON 응답에서 실제 답변 텍스트만 추출 메서드
	private String extraContentFromResponse(String responseJson) {
		System.out.println("responseJson : " + responseJson);
		try {
			// Jackson JSON 파서
			ObjectMapper mapper = new ObjectMapper();
			// JSON 문자열을 트리구조로 변환
			JsonNode root = mapper.readTree(responseJson);
			// choices[0].message.content 경로의 값을 반환
//			return root.path("choices").get(0).path("message").path("content").asText();
			String markdown = root.path("choices").get(0).path("message").path("content").asText();
			return MarkdownUtil.toHtml(markdown);
			
		} catch (Exception e) {
			System.out.println("Error parsing JSON response: " + e.getMessage());
			return "Parsing error";
		}
	}
	
}
