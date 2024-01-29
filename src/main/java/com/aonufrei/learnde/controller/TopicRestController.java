package com.aonufrei.learnde.controller;

import com.aonufrei.learnde.dto.TopicOut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/topics")
public class TopicRestController {

	@GetMapping
	private ResponseEntity<List<TopicOut>> getAll() {
		return ResponseEntity.ok(Collections.emptyList());
	}

	@GetMapping("{id}")
	private ResponseEntity<TopicOut> getById() {
		return ResponseEntity.ok(null);
	}

	@PostMapping
	private ResponseEntity<TopicOut> create() {
		return ResponseEntity.ok(null);
	}

	@PutMapping
	private ResponseEntity<TopicOut> update() {
		return ResponseEntity.ok(null);
	}

	@DeleteMapping
	private ResponseEntity<Boolean> delete() {
		return ResponseEntity.ok(true);
	}

}
