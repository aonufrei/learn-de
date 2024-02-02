package com.aonufrei.learnde.controller;

import com.aonufrei.learnde.exceptions.FailedValidationException;
import com.aonufrei.learnde.exceptions.TopicNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler({TopicNotFoundException.class, FailedValidationException.class})
	public ResponseEntity<String> handleValidationException(RuntimeException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> handleOtherException() {
		return ResponseEntity.internalServerError().build();
	}

}
