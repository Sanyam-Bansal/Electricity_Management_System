package com.electric.serviceImpl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.electric.entities.EmailRequest;

@SpringBootTest
public class EmailServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmailServiceImpl emailService;

    private static final String EMAIL_SERVICE_URL = "http://localhost:9001/sendEmail";
    
    @Test
    public void testSendMail() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setBody("Test Body");
        emailRequest.setSubject("Test Subject");
        emailRequest.setToEmail("test@example.com");

        ResponseEntity<String> mockResponse = new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
        when(restTemplate.postForEntity(EMAIL_SERVICE_URL, emailRequest, String.class))
                .thenReturn(mockResponse);

        emailService.sendMail("test@example.com" , "Test Subject" , "Test Body" );

        verify(restTemplate, times(1)).postForEntity(EMAIL_SERVICE_URL, emailRequest, String.class);
    }
}