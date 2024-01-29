package com.aonufrei.learnde.controller;

import com.aonufrei.learnde.dto.WordOut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/words")
public class WordRestController {

	@GetMapping
	private ResponseEntity<List<WordOut>> getAll() {
		return ResponseEntity.ok(Collections.emptyList());
	}

	@GetMapping("{id}")
	private ResponseEntity<WordOut> getById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(null);
	}

	@PostMapping
	private ResponseEntity<WordOut> create() {
		return ResponseEntity.ok(null);
	}

	@PutMapping("{id}")
	private ResponseEntity<WordOut> update(@PathVariable("id") Long id) {
		return ResponseEntity.ok(null);
	}

	@DeleteMapping
	private ResponseEntity<Boolean> delete() {
		return ResponseEntity.ok(true);
	}

}
