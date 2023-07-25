package com.electric.entities;

import lombok.Data;

@Data
public class EmailRequest {
	String toEmail;
	String body;
	String subject;
	public EmailRequest(String toEmail, String body, String subject) {
		super();
		this.toEmail = toEmail;
		this.body = body;
		this.subject = subject;
	}
	
	public EmailRequest() {}
	
}
