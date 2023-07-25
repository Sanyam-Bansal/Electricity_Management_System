package com.electric.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.electric.entities.EmailRequest;
import com.electric.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService{
	
//	@Autowired
//    private JavaMailSender mailSender;

	@Autowired
	private RestTemplate restTemplate;


	public void sendMail(String to, String subject, String body) {
//		SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
		
		String emailServiceUrl = "http://localhost:9001/sendEmail";
	    EmailRequest emailRequest = new EmailRequest(to, body, subject);

	    ResponseEntity<String> response = restTemplate.postForEntity(emailServiceUrl, emailRequest, String.class);

//	    if (response.getStatusCode().is2xxSuccessful()) {
//	        return response.getBody();
//	    } else {
//	        throw new RuntimeException("Failed to send email: " + response.getBody());
//	    }
		
	}

}
