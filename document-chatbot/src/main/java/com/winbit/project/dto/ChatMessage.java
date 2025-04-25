package com.winbit.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Getter
//@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessage {
	private String role;	// "user" or "developer"
	private String content; // 메세지 내용
}
