package com.electric.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ExceptionAndValidationHandlerTest {

	@Mock
	private BindingResult bindingResult;

	@InjectMocks
	private ExceptionAndValidationHandler exceptionHandler;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void handleInvalidArguments_ReturnsErrorMapWithFieldErrors() {

		MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
		FieldError fieldError1 = new FieldError("fieldName1", "field1", "error message 1");
		FieldError fieldError2 = new FieldError("fieldName2", "field2", "error message 2");

		when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

		Map<String, String> expectedErrorMap = new HashMap<>();
		expectedErrorMap.put("field1", "error message 1");
		expectedErrorMap.put("field2", "error message 2");

		Map<String, String> errorMap = exceptionHandler.handleInvalidArguments(exception);

		assertEquals(expectedErrorMap, errorMap);
	}

	@Test
	void handleNotFoundArguments_ReturnsErrorMapWithErrorMessage() {

		NoSuchElementException exception = new NoSuchElementException("Element not found");

		Map<String, String> expectedErrorMap = new HashMap<>();
		expectedErrorMap.put("errorMessage", "Element not found");

		Map<String, String> errorMap = exceptionHandler.handleNotFoundArguments(exception);

		assertEquals(expectedErrorMap, errorMap);
	}
}
