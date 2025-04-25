package com.winbit.project.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${openai.api.key}")
	private String apiKey;
	
	@Value("${openai.api.url}")
	private String apiUrl;
	
	//Spring Boot에서 REST API 호출은 WebClient 또는 RestTemplate으로 할 수 있는데, 최신 스타일인 WebClient 사용을 추천
	@Bean
	public WebClient openAIWebClient() {
		return WebClient.builder()
					.baseUrl(apiUrl)
					.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey) // Bearer뒤에 공백 한칸 추가해야함
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.build();
	}
	
	
}
