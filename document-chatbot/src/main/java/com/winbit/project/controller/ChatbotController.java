package com.winbit.project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.winbit.project.dto.ChatMessage;
import com.winbit.project.service.OpenAIService;

@Controller
@SessionAttributes("chatHistory") // 이 이름의 데이터를 Model에서 꺼내 세션에 저장
public class ChatbotController {
	@Autowired
	private OpenAIService openAIService;
	
	//-------------------------------------------------------------------------------------------
	@GetMapping("/")
	public String home() {
		return "index";
	}
	//-------------------------------------------------------------------------------------------
	// 세션 기반으로 대화 이력 저장
	@ModelAttribute("chatHistory")
	public List<ChatMessage> chatHistory() {
		return new ArrayList<>();
	}
	
	//-------------------------------------------------------------------------------------------
	// 챗봇 질문시 답변(api) 처리
	@PostMapping("/chatbot/question")
	public String askChatbot(@RequestParam("question") String question, Model model,
							@ModelAttribute("chatHistory") List<ChatMessage> chatHistory) {
		//--------------------------------------------
		// 유저 질문 저장
//		System.out.println("질문 : " + question); 
		chatHistory.add(new ChatMessage("user", question));
		//--------------------------------------------
		// 답변 생성 - OpenAI API 호출
		String answer = openAIService.getChatAnswer(question).block(); 
		//참고) .block()는 결과가 나올 때까지 기다렸다가 반환(비동기를 동기처럼 처리)
//		System.out.println("결과 : " + answer);
		// 답변 저장
		chatHistory.add(new ChatMessage("developer", answer));
		//--------------------------------------------
//		model.addAttribute("answer", answer);
		model.addAttribute("chatHistory", chatHistory);
		// => 저장한 대화 이력을 모델에 담기
		return "chatbot"; // chatbot.html 
	}
	
	//-------------------------------------------------------------------------------------------
	// 챗봇 페이지 이동
	@GetMapping("/chatbot")
	public String chatbotPage(@ModelAttribute("chatHistory") List<ChatMessage> chatHistory, Model model) {
		
		model.addAttribute("chatHistory", chatHistory);
		return "chatbot"; // chatbot.html 
	}
	//-------------------------------------------------------------------------------------------
	// 대화 초기화
	@GetMapping("/chatbot/reset")
	public String resetChatHistory(SessionStatus status) {
		status.setComplete(); //세션 속성 초기화
		return "redirect:/chatbot";
	}
}
