package com.electric.IntegrationTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class BaseIntegrationTest {
	 @Container
	    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
	            .withDatabaseName("testdb")
	            .withUsername("testuser")
	            .withPassword("testpassword");

	    @DynamicPropertySource
	    static void setDataSourceProperties(DynamicPropertyRegistry registry) {
	        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
	        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
	        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	    }
	    
	    protected RestTemplate restTemplate;

	    public BaseIntegrationTest() {
	        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
	        int timeout = 5000; // set your desired timeout in milliseconds
	        httpRequestFactory.setConnectTimeout(timeout);
	        httpRequestFactory.setReadTimeout(timeout);

	        restTemplate = new RestTemplate(httpRequestFactory);
	    }
}
