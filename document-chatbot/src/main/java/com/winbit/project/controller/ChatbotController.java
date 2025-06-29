package com.winbit.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.winbit.project.dto.ChatMessage;
import com.winbit.project.service.OpenAIService;

@Controller
@SessionAttributes({"chatHistory", "pdfHistory"}) // 이 이름의 데이터를 Model에서 꺼내 세션에 저장
public class ChatbotController {

    private final WebClient openAIWebClient;
	@Autowired
	private OpenAIService openAIService;

    ChatbotController(WebClient openAIWebClient) {
        this.openAIWebClient = openAIWebClient;
    }
	
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
	// 챗봇 페이지 이동
	@GetMapping("/chatbot")
	public String chatbotPage(@ModelAttribute("chatHistory") List<ChatMessage> chatHistory, Model model) {
		model.addAttribute("chatHistory", chatHistory);
		return "chatbot"; // chatbot.html 
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
	// 챗봇 대화 초기화
	@GetMapping("/chatbot/reset")
	public String resetChatHistory(SessionStatus status) {
		status.setComplete(); //세션 속성 초기화
		return "redirect:/chatbot";
	}
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//세션에 해당 키(pdfHistory)가 없을 때는 이 초기값이 사용
	@ModelAttribute("pdfHistory")
	public List<ChatMessage> pdfHistory() {
	    return new ArrayList<>();
	}
	//-------------------------------------------------------------------------------------------
	// PDF 요약 페이지 이동
	@GetMapping("/pdfSummary")
	public String pdfSummaryPage(@ModelAttribute("pdfHistory") List<ChatMessage> pdfHistory, Model model) {
		model.addAttribute("pdfHistory", pdfHistory);
		return "pdf_summary"; // pdf_summary.html 
	}
	//-------------------------------------------------------------------------------------------
	// pdf 첨부파일 업로드 시 텍스트 추출 (Apache PDFBox 활용)하여 문서 요약 기능 추가
	@PostMapping("/pdf/upload")
	public String uploadPdf(@RequestParam("file") MultipartFile file,
							@ModelAttribute("pdfHistory") List<ChatMessage> pdfHistory,
							Model model) throws IOException {
		
		System.out.println("file : " + file.getName() + file.getContentType());
		
		//파일 확장자 확인
		if(file.isEmpty() ) {
			pdfHistory.add(new ChatMessage("developer", "올바른 pdf 파일을 업로드해주세요."));
			return "chatbot";
		}
		
		// 1) PDF 텍스트 추출
		PDDocument document = PDDocument.load(file.getInputStream());
		PDFTextStripper stripper = new PDFTextStripper();
		String text = stripper.getText(document);
		document.close();
//		// 텍스트를 요약하여 세션 이력에 표시
//		chatHistory.add(new ChatMessage("developer", "파일이 성공적으로 업로드 되었습니다. 추출된 내용 일부 :\n\n" +
//					text.substring(0, Math.min(1000, text.length())) + (text.length() > 1000 ? "..." : "")));
//		
//		//또는, OpenAI에게 요약하거나 분석 요청 가능
//		String answer = openAIService.getChatAnswer("이 문서를 요약해줘 : " + text).block();
//		System.out.println("OpenAI에게 요약 요청 결과 : " + answer);
		
		
		// 2) OpenAI 요약 요청
		String prompt = "다음 PDF 문서 내용을 간단하게 요약해줘.\n\n" + text;
		String summary = openAIService.getChatAnswer(prompt).block();		
		
		// 3) 요약된 답변 챗본 이력에 추가
		pdfHistory.add(new ChatMessage("developer", "<h3>PDF 요약 결과</h3>\n\n"+ summary));
		model.addAttribute("pdfHistory", pdfHistory);
		return "pdf_summary";
	}
	//-------------------------------------------------------------------------------------------
	// 요약 초기화
	@GetMapping("/pdfSummary/reset")
	public String resetPdfHistory(SessionStatus status) {
		status.setComplete(); //세션 속성 초기화
		// 원하는 속성만 초기화하려면 별도로 model.asMap().remove("pdfHistory")를 사용
		return "redirect:/pdfSummary";
	}
}
