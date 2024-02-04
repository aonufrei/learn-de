package com.aonufrei.learnde.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRestController {

	@GetMapping("/health")
	private String health() {
		return "I am ok!";
	}

}
